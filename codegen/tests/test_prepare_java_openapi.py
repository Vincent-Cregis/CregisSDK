import importlib.util
import json
import tempfile
import unittest
from pathlib import Path


SCRIPT_PATH = Path(__file__).resolve().parents[1] / "scripts" / "prepare-java-openapi.py"
MODULE_SPEC = importlib.util.spec_from_file_location("prepare_java_openapi", SCRIPT_PATH)
prepare_java_openapi = importlib.util.module_from_spec(MODULE_SPEC)
assert MODULE_SPEC.loader is not None
MODULE_SPEC.loader.exec_module(prepare_java_openapi)


class PrepareJavaOpenApiTest(unittest.TestCase):

    def test_strips_auth_unwraps_data_and_names_nested_models(self):
        spec = self.sample_spec()
        with tempfile.TemporaryDirectory() as temp_dir:
            spec_path = Path(temp_dir) / "sample.json"
            spec_path.write_text(json.dumps(spec), encoding="utf-8")
            prepared, lock = prepare_java_openapi.prepare_api(
                "waas",
                spec_path,
                spec,
                {
                    "operations": [{
                        "operationId": "listThings",
                        "method": "post",
                        "path": "/things",
                    }]
                },
                {
                    "modelPackage": "com.cregis.sdk.generated.waas.model",
                    "componentNames": {},
                    "schemaNames": {
                        "listThings.response.rows.items": "Thing",
                    },
                    "operations": {
                        "listThings": {
                            "requestModel": "ListThingsRequest",
                            "responseModel": "ListThingsResponse",
                        }
                    },
                },
                {"pid", "nonce", "timestamp", "sign"},
            )

        request = prepared["components"]["schemas"]["ListThingsRequest"]
        self.assertEqual(["query"], request["required"])
        self.assertEqual({"query", "filter"}, set(request["properties"]))
        self.assertNotIn("pid", request["properties"])
        self.assertNotIn("sign", request["properties"])
        self.assertEqual(
            "#/components/schemas/ListThingsRequestFilter",
            request["properties"]["filter"]["$ref"],
        )

        response = prepared["components"]["schemas"]["ListThingsResponse"]
        self.assertEqual(
            "#/components/schemas/Thing",
            response["properties"]["rows"]["items"]["$ref"],
        )
        self.assertEqual(
            {"ListThingsRequest", "ListThingsRequestFilter", "ListThingsResponse", "Thing"},
            set(lock["models"]),
        )

    def test_rejects_manifest_operation_mismatch(self):
        spec = self.sample_spec()
        with tempfile.TemporaryDirectory() as temp_dir:
            spec_path = Path(temp_dir) / "sample.json"
            spec_path.write_text(json.dumps(spec), encoding="utf-8")
            with self.assertRaisesRegex(
                prepare_java_openapi.PreparationError,
                "model configuration mismatch",
            ):
                prepare_java_openapi.prepare_api(
                    "waas",
                    spec_path,
                    spec,
                    {"operations": [{
                        "operationId": "listThings",
                        "method": "post",
                        "path": "/things",
                    }]},
                    {
                        "modelPackage": "example",
                        "operations": {},
                    },
                    {"pid", "nonce", "timestamp", "sign"},
                )

    def test_rejects_spec_operation_id_mismatch(self):
        spec = self.sample_spec()
        spec["paths"]["/things"]["post"]["operationId"] = "renamedThing"
        with tempfile.TemporaryDirectory() as temp_dir:
            spec_path = Path(temp_dir) / "sample.json"
            spec_path.write_text(json.dumps(spec), encoding="utf-8")
            with self.assertRaisesRegex(
                prepare_java_openapi.PreparationError,
                "Operation ID mismatch",
            ):
                prepare_java_openapi.prepare_api(
                    "waas",
                    spec_path,
                    spec,
                    {"operations": [{
                        "operationId": "listThings",
                        "method": "post",
                        "path": "/things",
                    }]},
                    {
                        "modelPackage": "example",
                        "componentNames": {},
                        "schemaNames": {},
                        "operations": {
                            "listThings": {
                                "requestModel": "ListThingsRequest",
                                "responseModel": "ListThingsResponse",
                            }
                        },
                    },
                    {"pid", "nonce", "timestamp", "sign"},
                )

    def test_rejects_non_empty_output_directory(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            (root / "codegen/configs").mkdir(parents=True)
            (root / "specs").mkdir()
            output = root / "output"
            output.mkdir()
            (output / "keep.txt").write_text("keep", encoding="utf-8")

            with self.assertRaisesRegex(
                prepare_java_openapi.PreparationError,
                "Output directory must be empty",
            ):
                prepare_java_openapi.prepare(root / "specs", output, root)

    @staticmethod
    def sample_spec():
        return {
            "openapi": "3.1.0",
            "info": {"title": "Sample", "version": "1.0.0"},
            "paths": {
                "/things": {
                    "post": {
                        "operationId": "listThings",
                        "requestBody": {
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "allOf": [
                                            {"$ref": "#/components/schemas/AuthFields"},
                                            {
                                                "type": "object",
                                                "required": ["pid", "query"],
                                                "properties": {
                                                    "pid": {"type": "integer", "format": "int64"},
                                                    "query": {"type": "string"},
                                                    "filter": {
                                                        "type": "object",
                                                        "properties": {"active": {"type": "boolean"}},
                                                    },
                                                },
                                            },
                                        ]
                                    }
                                }
                            }
                        },
                        "responses": {
                            "200": {
                                "content": {
                                    "application/json": {
                                        "schema": {
                                            "allOf": [
                                                {"$ref": "#/components/schemas/StandardResponse"},
                                                {
                                                    "type": "object",
                                                    "properties": {
                                                        "data": {
                                                            "type": "object",
                                                            "properties": {
                                                                "rows": {
                                                                    "type": "array",
                                                                    "items": {
                                                                        "type": "object",
                                                                        "properties": {
                                                                            "id": {"type": "string"}
                                                                        },
                                                                    },
                                                                }
                                                            },
                                                        }
                                                    },
                                                },
                                            ]
                                        }
                                    }
                                }
                            }
                        },
                    }
                }
            },
            "components": {
                "schemas": {
                    "AuthFields": {
                        "type": "object",
                        "required": ["nonce", "timestamp", "sign"],
                        "properties": {
                            "nonce": {"type": "string"},
                            "timestamp": {"type": "integer", "format": "int64"},
                            "sign": {"type": "string"},
                        },
                    },
                    "StandardResponse": {
                        "type": "object",
                        "properties": {
                            "code": {"type": "string"},
                            "msg": {"type": "string"},
                            "data": {},
                        },
                    },
                }
            },
        }


if __name__ == "__main__":
    unittest.main()
