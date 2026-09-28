import os
import glob
import re

base_dir = r"c:\thayHoa`\petcare\src\main\resources\templates\admin"
# Fix the backtick in path if running from python inside Windows
base_dir = base_dir.replace("`", "") # wait, the folder on disk actually has a backtick?
# The path is c:\thayHoa`\petcare
base_dir = r"c:\thayHoa`\petcare\src\main\resources\templates\admin"

html_files = []
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".html"):
            path = os.path.join(root, file)
            # Skip login, dashboard, and fragments
            if "login.html" in path or "dashboard.html" in path or "fragments" in path:
                continue
            html_files.append(path)

for file_path in html_files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    # Extract title
    title_match = re.search(r"<title>\s*(.*?)\s*</title>", content, re.IGNORECASE | re.DOTALL)
    title = title_match.group(1).strip() if title_match else "Admin - PetCare"
    
    # Extract body inner HTML
    body_match = re.search(r"<body>(.*?)</body>", content, re.IGNORECASE | re.DOTALL)
    if not body_match:
        continue
    body_inner = body_match.group(1)
    
    # Cleanup old elements
    body_inner = re.sub(r'<a href="/admin/dashboard".*?Quay lại Dashboard.*?</a>', '', body_inner, flags=re.IGNORECASE | re.DOTALL)
    body_inner = re.sub(r'<hr>', '', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'<br/?>', '', body_inner, flags=re.IGNORECASE)
    
    # Replace table
    body_inner = re.sub(r'<table[^>]*>', '<div class="table-container">\n<table>', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'</table>', '</table>\n</div>', body_inner, flags=re.IGNORECASE)
    
    # Add classes
    body_inner = re.sub(r'<button type="submit"(?!.*class)', '<button type="submit" class="btn btn-primary"', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'<a href="[^"]*/create"[^>]*>', lambda m: m.group(0).replace('>', ' class="btn btn-primary" style="margin-bottom: 20px;">'), body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'<input type="(text|number|password|email)"', r'<input type="\1" class="form-control"', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'<textarea', r'<textarea class="form-control"', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'<select', r'<select class="form-control"', body_inner, flags=re.IGNORECASE)
    
    # Forms wrapper
    body_inner = re.sub(r'<label>', '<label class="form-label">', body_inner, flags=re.IGNORECASE)
    
    # Alerts
    body_inner = re.sub(r'style="color:\s*green;?"', 'class="alert alert-success"', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'style="color:\s*red;?"', 'class="alert alert-error"', body_inner, flags=re.IGNORECASE)
    
    # Format h1 as h2 to fit layout
    body_inner = re.sub(r'<h1(>| )', r'<h2 style="margin-bottom: 20px;"\1', body_inner, flags=re.IGNORECASE)
    body_inner = re.sub(r'</h1>', '</h2>', body_inner, flags=re.IGNORECASE)

    new_content = f"""<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{{admin/fragments/admin-layout :: layout(~{{::title}}, ~{{::section}})}}">
<head>
    <title>{title}</title>
</head>
<body>
    <section>
        <div style="background: white; padding: 30px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            {body_inner.strip()}
        </div>
    </section>
</body>
</html>"""
    
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    
    print(f"Refactored: {file_path}")

print("Done refactoring 14 files.")
