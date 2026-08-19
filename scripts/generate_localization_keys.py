import sys
import re
from pathlib import Path
import yaml

def clean_identifier(name: str) -> str:
    return re.sub(r'[^A-Za-z0-9]', '_', name).upper()

def to_pascal_case(name: str) -> str:
    return "".join(part.capitalize() for part in re.split(r'[^A-Za-z0-9]', name) if part)

def process_section(prefix_path: str, data: dict, indent_level: int = 1) -> list[str]:
    indent = "    " * indent_level
    lines = []

    for key, value in data.items():
        full_path = f"{prefix_path}.{key}" if prefix_path else key

        if isinstance(value, dict):
            inner_class = to_pascal_case(key)
            lines.append(f"{indent}public static final class {inner_class} {{")
            lines.extend(process_section(full_path, value, indent_level + 1))
            lines.append(f"{indent}}}")
        else:
            field_name = clean_identifier(key)
            lines.append(f'{indent}public static final String {field_name} = "{full_path}";')

    return lines

def write_java_class(output_dir: Path, package_name: str, class_name: str, lines: list[str]):
    """Helper to eliminate code duplication when writing Java files."""
    header = [
        f"package {package_name};\n",
        "// AUTO-GENERATED FILE. DO NOT EDIT MANUALLY.",
        f"public final class {class_name} {{",
        f"    private {class_name}() {{}}"
    ]
    content = "\n".join(header + lines + ["}\n"])
    (output_dir / f"{class_name}.java").write_text(content, encoding="utf-8")

def generate_classes(yaml_path: str, output_dir: str, package_name: str):
    yaml_file, out_path = Path(yaml_path), Path(output_dir)

    if not yaml_file.is_file():
        print(f"Warning: Localization YAML file not found at {yaml_path}. Skipping generation.")
        return

    out_path.mkdir(parents=True, exist_ok=True)
    data = yaml.safe_load(yaml_file.read_text(encoding="utf-8")) or {}

    general_keys = {}

    for section_name, content in data.items():
        if isinstance(content, dict):
            class_name = f"{to_pascal_case(section_name)}"
            lines = process_section(section_name, content)
            write_java_class(out_path, package_name, class_name, lines)
        else:
            general_keys[section_name] = content

    if general_keys:
        lines = process_section("", general_keys)
        write_java_class(out_path, package_name, "General", lines)

if __name__ == "__main__":
    if len(sys.argv) < 4:
        sys.exit(1)
    generate_classes(sys.argv[1], sys.argv[2], sys.argv[3])