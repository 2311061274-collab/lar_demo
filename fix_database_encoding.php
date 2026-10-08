<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

function fix_vietnamese_mojibake($str) {
    if (empty($str) || !is_string($str)) return $str;
    static $revMap = null;
    if ($revMap === null) {
        $revMap = [];
        for ($b = 0; $b < 256; $b++) {
            $char = chr($b);
            $u = @iconv('WINDOWS-1258', 'UTF-8', $char);
            if ($u !== false && $u !== '') {
                $revMap[$u] = $char;
            }
            $u2 = @iconv('ISO-8859-1', 'UTF-8', $char);
            if ($u2 !== false && $u2 !== '' && !isset($revMap[$u2])) {
                $revMap[$u2] = $char;
            }
            $revMap["\xC2\x90"] = chr(0x90);
            $revMap["\xC2\x81"] = chr(0x81);
            $revMap["\xC2\x8D"] = chr(0x8D);
            $revMap["\xC2\x8E"] = chr(0x8E);
            $revMap["\xC2\x8F"] = chr(0x8F);
        }
    }
    
    $bytes = '';
    $len = strlen($str);
    for ($i = 0; $i < $len; ) {
        $sub3 = substr($str, $i, 3);
        $sub2 = substr($str, $i, 2);
        if (isset($revMap[$sub3])) {
            $bytes .= $revMap[$sub3];
            $i += 3;
        } elseif (isset($revMap[$sub2])) {
            $bytes .= $revMap[$sub2];
            $i += 2;
        } else {
            $bytes .= $str[$i];
            $i += 1;
        }
    }
    
    if (mb_check_encoding($bytes, 'UTF-8')) {
        return $bytes;
    }
    return $str;
}

echo "1. Fixing messages...\n";
if (Schema::hasTable('messages')) {
    foreach (DB::table('messages')->get() as $row) {
        if (!empty($row->content)) {
            $fixed = fix_vietnamese_mojibake($row->content);
            if ($fixed !== $row->content) {
                DB::table('messages')->where('id', $row->id)->update(['content' => $fixed]);
            }
        }
    }
}

echo "2. Fixing users...\n";
if (Schema::hasTable('users')) {
    foreach (DB::table('users')->get() as $row) {
        $up = [];
        if (!empty($row->name)) {
            $fixed = fix_vietnamese_mojibake($row->name);
            if ($fixed !== $row->name) $up['name'] = $fixed;
        }
        if (!empty($row->address)) {
            $fixed = fix_vietnamese_mojibake($row->address);
            if ($fixed !== $row->address) $up['address'] = $fixed;
        }
        if (!empty($up)) {
            DB::table('users')->where('id', $row->id)->update($up);
        }
    }
}

echo "3. Fixing orders...\n";
if (Schema::hasTable('orders')) {
    foreach (DB::table('orders')->get() as $row) {
        $up = [];
        $cols = ['recipient_name', 'province_name', 'district_name', 'ward_name', 'address_detail'];
        foreach ($cols as $col) {
            if (!empty($row->$col)) {
                $fixed = fix_vietnamese_mojibake($row->$col);
                if ($fixed !== $row->$col) $up[$col] = $fixed;
            }
        }
        if (!empty($up)) {
            DB::table('orders')->where('id', $row->id)->update($up);
        }
    }
}

echo "4. Fixing user_addresses...\n";
if (Schema::hasTable('user_addresses')) {
    foreach (DB::table('user_addresses')->get() as $row) {
        $up = [];
        $cols = ['name', 'province', 'district', 'address'];
        foreach ($cols as $col) {
            if (!empty($row->$col)) {
                $fixed = fix_vietnamese_mojibake($row->$col);
                if ($fixed !== $row->$col) $up[$col] = $fixed;
            }
        }
        if (!empty($up)) {
            DB::table('user_addresses')->where('id', $row->id)->update($up);
        }
    }
}

echo "5. Fixing order_items...\n";
if (Schema::hasTable('order_items')) {
    foreach (DB::table('order_items')->get() as $row) {
        if (!empty($row->product_name)) {
            $fixed = fix_vietnamese_mojibake($row->product_name);
            if ($fixed !== $row->product_name) {
                DB::table('order_items')->where('id', $row->id)->update(['product_name' => $fixed]);
            }
        }
    }
}

echo "6. Fixing categories...\n";
if (Schema::hasTable('categories')) {
    foreach (DB::table('categories')->get() as $row) {
        $up = [];
        if (!empty($row->name)) {
            $fixed = fix_vietnamese_mojibake($row->name);
            if ($fixed !== $row->name) $up['name'] = $fixed;
        }
        if (!empty($row->description)) {
            $fixed = fix_vietnamese_mojibake($row->description);
            if ($fixed !== $row->description) $up['description'] = $fixed;
        }
        if (!empty($up)) {
            DB::table('categories')->where('id', $row->id)->update($up);
        }
    }
}

echo "7. Fixing products...\n";
if (Schema::hasTable('products')) {
    foreach (DB::table('products')->get() as $row) {
        $up = [];
        $cols = ['name', 'description', 'focal_length', 'mount', 'brand'];
        foreach ($cols as $col) {
            if (!empty($row->$col)) {
                $fixed = fix_vietnamese_mojibake($row->$col);
                if ($fixed !== $row->$col) $up[$col] = $fixed;
            }
        }
        if (!empty($up)) {
            DB::table('products')->where('id', $row->id)->update($up);
        }
    }
}

echo "8. Fixing news...\n";
if (Schema::hasTable('news')) {
    foreach (DB::table('news')->get() as $row) {
        $up = [];
        $cols = ['title', 'description', 'content', 'tag_text', 'author'];
        foreach ($cols as $col) {
            if (!empty($row->$col)) {
                $fixed = fix_vietnamese_mojibake($row->$col);
                if ($fixed !== $row->$col) $up[$col] = $fixed;
            }
        }
        if (!empty($up)) {
            DB::table('news')->where('id', $row->id)->update($up);
        }
    }
}

echo "9. Fixing vouchers...\n";
if (Schema::hasTable('vouchers')) {
    foreach (DB::table('vouchers')->get() as $row) {
        $up = [];
        if (isset($row->name)) $up['name'] = fix_vietnamese_mojibake($row->name);
        if (isset($row->description)) $up['description'] = fix_vietnamese_mojibake($row->description);
        if (!empty($up)) DB::table('vouchers')->where('id', $row->id)->update($up);
    }
}

echo "10. Fixing return_requests...\n";
if (Schema::hasTable('return_requests')) {
    foreach (DB::table('return_requests')->get() as $row) {
        $up = [];
        if (isset($row->reason)) $up['reason'] = fix_vietnamese_mojibake($row->reason);
        if (isset($row->admin_note)) $up['admin_note'] = fix_vietnamese_mojibake($row->admin_note);
        if (!empty($up)) DB::table('return_requests')->where('id', $row->id)->update($up);
    }
}

echo "11. Fixing reviews...\n";
if (Schema::hasTable('reviews')) {
    foreach (DB::table('reviews')->get() as $row) {
        $up = [];
        if (isset($row->comment)) $up['comment'] = fix_vietnamese_mojibake($row->comment);
        if (!empty($up)) DB::table('reviews')->where('id', $row->id)->update($up);
    }
}

echo "12. Fixing customer_notes...\n";
if (Schema::hasTable('customer_notes')) {
    foreach (DB::table('customer_notes')->get() as $row) {
        $up = [];
        if (isset($row->content)) $up['content'] = fix_vietnamese_mojibake($row->content);
        if (!empty($up)) DB::table('customer_notes')->where('id', $row->id)->update($up);
    }
}

echo "All tables fixed successfully!\n";
