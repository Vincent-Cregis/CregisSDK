import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]
SCRIPT = REPO_ROOT / "codegen" / "scripts" / "check-java-openapi.py"


class JavaOpenApiDriftCheckTest(unittest.TestCase):

    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.spec_dir = self.root / "specs"
        self.java_root = self.root / "java"
        self.spec_dir.mkdir()
        (self.java_root / "example").mkdir(parents=True)
        self.manifest = self.root / "manifest.json"

        self.write_spec({"doThing": ("post", "/v1/do")})
        self.write_manifest("/v1/do")
        self.write_client("/v1/do")

    def tearDown(self):
        self.temp.cleanup()

    def write_spec(self, operations):
        paths = {}
        for operation_id, (method, path) in operations.items():
            paths.setdefault(path, {})[method] = {"operationId": operation_id}
        (self.spec_dir / "api.json").write_text(
            json.dumps({"openapi": "3.1.0", "paths": paths}),
            encoding="utf-8",
        )

    def write_manifest(self, path):
        self.manifest.write_text(
            json.dumps({
                "version": 1,
                "apis": {
                    "example": {
                        "specFile": "api.json",
                        "clientSource": "example/ExampleClient.java",
                        "operations": [{
                            "operationId": "doThing",
                            "method": "post",
                            "path": path,
                            "clientMethod": "doThing",
                        }],
                    },
                },
            }),
            encoding="utf-8",
        )

    def write_client(self, path):
        (self.java_root / "example" / "ExampleClient.java").write_text(
            "public class ExampleClient {\n"
            "    public Object doThing(Object request) {\n"
            f"        return execute(post(\"{path}\", request));\n"
            "    }\n"
            "}\n",
            encoding="utf-8",
        )

    def run_check(self):
        return subprocess.run(
            [
                sys.executable,
                str(SCRIPT),
                "--spec-dir", str(self.spec_dir),
                "--manifest", str(self.manifest),
                "--java-source-root", str(self.java_root),
            ],
            text=True,
            capture_output=True,
            check=False,
        )

    def test_passes_when_spec_manifest_and_java_match(self):
        result = self.run_check()

        self.assertEqual(0, result.returncode, result.stderr)
        self.assertIn("total 1", result.stdout)

    def test_reports_new_openapi_operation(self):
        self.write_spec({
            "doThing": ("post", "/v1/do"),
            "newThing": ("post", "/v1/new"),
        })

        result = self.run_check()

        self.assertEqual(1, result.returncode)
        self.assertIn("OpenAPI added POST /v1/new (newThing)", result.stderr)

    def test_reports_java_path_drift(self):
        self.write_client("/v1/wrong")

        result = self.run_check()

        self.assertEqual(1, result.returncode)
        self.assertIn("Java method doThing uses /v1/wrong, expected /v1/do", result.stderr)
        self.assertIn("untracked POST path /v1/wrong", result.stderr)


if __name__ == "__main__":
    unittest.main()
