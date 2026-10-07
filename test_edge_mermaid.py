import subprocess
import pathlib

html = """<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<script src="https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js"></script>
<script>
mermaid.initialize({ startOnLoad: true, theme: 'default' });
</script>
</head>
<body>
<h1>Test Diagram</h1>
<div class="mermaid">
flowchart TD
    A[Khách hàng] --> B(Đặt lịch hẹn)
    B --> C{Xác nhận?}
    C -->|Có| D[Thành công]
    C -->|Không| E[Hủy]
</div>
</body>
</html>
"""

test_html = pathlib.Path("test_mmd.html")
test_html.write_text(html, encoding="utf-8")

edge_path = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
cmd = [
    edge_path,
    "--headless",
    "--disable-gpu",
    "--virtual-time-budget=6000",
    "--run-all-compositor-stages-before-draw",
    f"--print-to-pdf={pathlib.Path('test_mmd.pdf').resolve()}",
    str(test_html.resolve())
]

res = subprocess.run(cmd, capture_output=True, text=True)
pdf_path = pathlib.Path("test_mmd.pdf")
print("PDF created:", pdf_path.exists(), "size:", pdf_path.stat().st_size if pdf_path.exists() else 0)
