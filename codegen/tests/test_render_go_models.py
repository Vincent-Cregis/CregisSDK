import importlib.util
import unittest
from pathlib import Path


SCRIPT_PATH = Path(__file__).resolve().parents[1] / "scripts" / "render-go-models.py"
MODULE_SPEC = importlib.util.spec_from_file_location("render_go_models", SCRIPT_PATH)
render_go_models = importlib.util.module_from_spec(MODULE_SPEC)
assert MODULE_SPEC.loader is not None
MODULE_SPEC.loader.exec_module(render_go_models)


class RenderGoModelsTest(unittest.TestCase):

    def test_generates_named_enums_and_validates_request_and_output_boundaries(self):
        models, validation, directions = render_go_models.render_models(
            "sample", self.sample_document()
        )

        self.assertIn("type SearchRequestMode string", models)
        self.assertIn(
            'SearchRequestModeExact SearchRequestMode = "exact"', models
        )
        self.assertIn("Mode *SearchRequestMode", models)
        self.assertIn("type ItemStatus int32", models)
        self.assertIn("ItemStatus1 ItemStatus = 1", models)

        self.assertEqual("request", directions["SearchRequest"])
        self.assertEqual("output", directions["SearchResponse"])
        self.assertIn('return fmt.Errorf("unknown field %q", field)', validation)
        self.assertIn(
            "unmarshalModel(data, &value, []string{}, nil, map[string]struct{}",
            validation,
        )
        self.assertIn("func (model Item) Validate() error", validation)
        self.assertIn("status must be one of the documented values", validation)
        self.assertIn("code does not match its OpenAPI pattern", validation)
        self.assertIn("(model.Rows)[index].Validate()", validation)
        self.assertIn("if err := decoded.Validate(); err != nil", validation)

    def test_enum_aliases_are_exported_from_the_root_package(self):
        document = self.sample_document()
        aliases = render_go_models.render_aliases(
            "example.test/sdk",
            {"sample": sorted(document["components"]["schemas"])},
            {"sample": document},
        )

        self.assertIn(
            "type SearchRequestMode = samplemodels.SearchRequestMode", aliases
        )
        self.assertIn(
            "SearchRequestModeExact = samplemodels.SearchRequestModeExact", aliases
        )
        self.assertIn("type ItemStatus = samplemodels.ItemStatus", aliases)

    @staticmethod
    def sample_document():
        return {
            "components": {
                "schemas": {
                    "SearchRequest": {
                        "type": "object",
                        "properties": {
                            "mode": {
                                "type": "string",
                                "enum": ["exact", "prefix"],
                            }
                        },
                    },
                    "SearchResponse": {
                        "type": "object",
                        "required": ["rows"],
                        "properties": {
                            "rows": {
                                "type": "array",
                                "items": {"$ref": "#/components/schemas/Item"},
                            }
                        },
                    },
                    "Item": {
                        "type": "object",
                        "required": ["status", "code"],
                        "properties": {
                            "status": {
                                "type": "integer",
                                "format": "int32",
                                "enum": [1, 2],
                            },
                            "code": {
                                "type": "string",
                                "pattern": "^[A-Z]+$",
                            },
                        },
                    },
                }
            },
            "x-cregis-operations": [
                {
                    "operationId": "search",
                    "path": "/search",
                    "method": "post",
                    "requestModel": "SearchRequest",
                    "responseModel": "SearchResponse",
                }
            ],
            "x-cregis-webhooks": [],
        }


if __name__ == "__main__":
    unittest.main()
