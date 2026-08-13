import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]
SCRIPT = REPO_ROOT / "codegen" / "scripts" / "check-java-generated-models.py"


class JavaGeneratedModelsCheckTest(unittest.TestCase):

    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        (self.root / "codegen/configs").mkdir(parents=True)
        (self.root / "codegen/manifests").mkdir(parents=True)
        (self.root / "codegen/scripts").mkdir(parents=True)
        self.generated = (
            self.root
            / "sdks/java/src/generated/java/com/cregis/sdk/generated/example/model"
        )
        self.generated.mkdir(parents=True)
        self.client = (
            self.root
            / "sdks/java/src/main/java/com/cregis/sdk/client/ExampleClient.java"
        )
        self.client.parent.mkdir(parents=True)

        operations = {
            "version": 1,
            "apis": {
                "example": {
                    "specFile": "example.json",
                    "operations": [{
                        "operationId": "doThing",
                        "method": "post",
                        "path": "/things",
                    }],
                }
            },
        }
        models = {
            "version": 1,
            "sdkManagedRequestFields": ["pid", "nonce", "timestamp", "sign"],
            "apis": {
                "example": {
                    "modelPackage": "com.cregis.sdk.generated.example.model",
                    "componentNames": {},
                    "schemaNames": {},
                    "operations": {
                        "doThing": {
                            "requestModel": "ThingRequest",
                            "responseModel": "ThingResponse",
                        }
                    },
                }
            },
        }
        lock = {
            "version": 1,
            "generator": {
                "image": "openapitools/openapi-generator-cli",
                "version": "7.19.0",
            },
            "apis": {
                "example": {
                    "inputSha256": "abc",
                    "modelPackage": "com.cregis.sdk.generated.example.model",
                    "models": ["ThingRequest", "ThingResponse"],
                    "operations": [{
                        "method": "post",
                        "operationId": "doThing",
                        "path": "/things",
                        "requestModel": "ThingRequest",
                        "responseContainer": None,
                        "responseModel": "ThingResponse",
                    }],
                    "specFile": "example.json",
                }
            },
        }
        self.write_json("codegen/configs/openapi-operations.json", operations)
        self.write_json("codegen/configs/openapi-models.json", models)
        self.write_json("codegen/configs/java-overrides.json", {
            "version": 1,
            "apis": {
                "example": {
                    "clientSource": "com/cregis/sdk/client/ExampleClient.java",
                    "clientMethods": {"doThing": "doThing"},
                    "modelPackage": "com.cregis.sdk.generated.example.model",
                },
            },
        })
        self.write_json("codegen/manifests/java-models.lock.json", lock)
        (self.root / "codegen/scripts/generate-java-models.sh").write_text(
            'GENERATOR_IMAGE="openapitools/openapi-generator-cli:7.19.0@sha256:'
            + "a" * 64
            + '"\n',
            encoding="utf-8",
        )
        self.write_model("ThingRequest", "public final class ThingRequest {}\n")
        self.write_model("ThingResponse", "public final class ThingResponse {}\n")
        self.client.write_text(
            "import com.cregis.sdk.generated.example.model.ThingRequest;\n"
            "import com.cregis.sdk.generated.example.model.ThingResponse;\n"
            "public final class ExampleClient {}\n",
            encoding="utf-8",
        )

    def tearDown(self):
        self.temp.cleanup()

    def write_json(self, relative_path, value):
        (self.root / relative_path).write_text(json.dumps(value), encoding="utf-8")

    def write_model(self, name, source):
        (self.generated / f"{name}.java").write_text(source, encoding="utf-8")

    def run_check(self):
        return subprocess.run(
            [sys.executable, str(SCRIPT), "--repo-root", str(self.root)],
            text=True,
            capture_output=True,
            check=False,
        )

    def test_passes_for_consistent_generated_boundary(self):
        result = self.run_check()

        self.assertEqual(0, result.returncode, result.stderr)
        self.assertIn("2 models, 1 operations", result.stdout)

    def test_rejects_sdk_managed_field_in_request_model(self):
        self.write_model(
            "ThingRequest",
            'public static final String JSON_PROPERTY_PID = "pid";\n',
        )

        result = self.run_check()

        self.assertEqual(1, result.returncode)
        self.assertIn("field 'pid' leaked into request model", result.stderr)

    def test_rejects_handwritten_duplicate(self):
        duplicate = (
            self.root
            / "sdks/java/src/main/java/com/cregis/sdk/webhook/example/model/ThingRequest.java"
        )
        duplicate.parent.mkdir(parents=True)
        duplicate.write_text("public final class ThingRequest {}\n", encoding="utf-8")

        result = self.run_check()

        self.assertEqual(1, result.returncode)
        self.assertIn("Handwritten operation models duplicate generated models", result.stderr)


if __name__ == "__main__":
    unittest.main()
