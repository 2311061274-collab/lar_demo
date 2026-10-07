<?php
$caPath = getenv('MYSQL_ATTR_SSL_CA');
if (!$caPath || !file_exists($caPath)) {
    fwrite(STDERR, "SSL CA file not found: {$caPath}\n");
    exit(1);
}
$cert = file_get_contents($caPath);
if (openssl_x509_parse($cert) === false) {
    fwrite(STDERR, "Invalid CA certificate format in {$caPath}\n");
    exit(1);
}