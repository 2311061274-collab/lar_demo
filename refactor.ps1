$baseDir = "c:\thayHoa``\petcare\src\main\resources\templates\admin"

# Note: In PowerShell string literals, the backtick must be escaped with another backtick.
# But wait, earlier I just used "c:\thayHoa`\petcare", wait, if it's single quotes, it's literal.
$baseDir = 'c:\thayHoa`\petcare\src\main\resources\templates\admin'

$files = Get-ChildItem -Path $baseDir -Filter *.html -Recurse | Where-Object { $_.FullName -notmatch 'login\.html' -and $_.FullName -notmatch 'dashboard\.html' -and $_.FullName -notmatch 'fragments' }

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8

    # Extract title
    $title = "Admin - PetCare"
    if ($content -match '(?si)<title>\s*(.*?)\s*</title>') {
        $title = $matches[1].Trim()
    }

    # Extract body inner HTML
    if ($content -match '(?si)<body>(.*?)</body>') {
        $bodyInner = $matches[1]

        # Cleanup
        $bodyInner = $bodyInner -replace '(?si)<a href="/admin/dashboard".*?Quay lại Dashboard.*?</a>', ''
        $bodyInner = $bodyInner -replace '(?i)<hr>', ''
        $bodyInner = $bodyInner -replace '(?i)<br/?>', ''

        # Tables
        $bodyInner = $bodyInner -replace '(?i)<table[^>]*>', '<div class="table-container"><table>'
        $bodyInner = $bodyInner -replace '(?i)</table>', '</table></div>'

        # Classes
        $bodyInner = $bodyInner -replace '(?i)<button type="submit"(?!.*class)', '<button type="submit" class="btn btn-primary"'
        $bodyInner = $bodyInner -replace '(?i)(<a href="[^"]*/create"[^>]*)>', '$1 class="btn btn-primary" style="margin-bottom: 20px;">'
        $bodyInner = $bodyInner -replace '(?i)<input type="(text|number|password|email)"', '<input type="$1" class="form-control"'
        $bodyInner = $bodyInner -replace '(?i)<textarea', '<textarea class="form-control"'
        $bodyInner = $bodyInner -replace '(?i)<select', '<select class="form-control"'
        
        $bodyInner = $bodyInner -replace '(?i)<label>', '<label class="form-label">'

        # Alerts
        $bodyInner = $bodyInner -replace '(?i)style="color:\s*green;?"', 'class="alert alert-success"'
        $bodyInner = $bodyInner -replace '(?i)style="color:\s*red;?"', 'class="alert alert-error"'
        
        # Headers
        $bodyInner = $bodyInner -replace '(?i)<h1(>| )', '<h2 style="margin-bottom: 20px;"$1'
        $bodyInner = $bodyInner -replace '(?i)</h1>', '</h2>'

        $newContent = @"
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{admin/fragments/admin-layout :: layout(~{::title}, ~{::section})}">
<head>
    <title>$title</title>
</head>
<body>
    <section>
        <div style="background: white; padding: 30px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            $($bodyInner.Trim())
        </div>
    </section>
</body>
</html>
"@

        Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8
        Write-Output "Refactored: $($file.FullName)"
    }
}

Write-Output "Done refactoring files."
