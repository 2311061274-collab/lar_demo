<?php

namespace App\Support;

class PermissionCatalog
{
    /**
     * Nhóm quyền theo nghiệp vụ (thứ tự hiển thị).
     */
    public static function groups(): array
    {
        return [
            'overview' => [
                'title' => 'Tổng quan',
                'desc'  => 'Trang điều khiển & báo cáo',
                'icon'  => 'fa-chart-pie',
                'perms' => ['view_dashboard', 'view_reports'],
            ],
            'commerce' => [
                'title' => 'Kinh doanh',
                'desc'  => 'Đơn hàng, sản phẩm, danh mục',
                'icon'  => 'fa-store',
                'perms' => ['manage_orders', 'manage_products', 'manage_categories'],
            ],
            'crm' => [
                'title' => 'Khách hàng & Marketing',
                'desc'  => 'CRM và mã giảm giá',
                'icon'  => 'fa-users',
                'perms' => ['manage_customers', 'manage_vouchers'],
            ],
            'system' => [
                'title' => 'Hệ thống',
                'desc'  => 'Tài khoản & phân quyền — quyền nhạy cảm',
                'icon'  => 'fa-shield-halved',
                'tone'  => 'danger',
                'perms' => ['manage_users', 'manage_roles'],
            ],
        ];
    }

    public static function meta(): array
    {
        return [
            'view_dashboard' => [
                'label' => 'Xem Dashboard',
                'icon'  => 'fa-chart-pie',
                'desc'  => 'Truy cập trang tổng quan',
                'color' => '#1a7a6d',
            ],
            'view_reports' => [
                'label' => 'Xem Báo cáo',
                'icon'  => 'fa-chart-column',
                'desc'  => 'Thống kê doanh thu, xuất CSV',
                'color' => '#c45c26',
            ],
            'manage_orders' => [
                'label' => 'Quản lý Đơn hàng',
                'icon'  => 'fa-boxes-packing',
                'desc'  => 'Xem & cập nhật trạng thái / GHN',
                'color' => '#b45309',
            ],
            'manage_products' => [
                'label' => 'Quản lý Sản phẩm',
                'icon'  => 'fa-camera',
                'desc'  => 'Thêm, sửa, xóa ống kính',
                'color' => '#1b7a4a',
            ],
            'manage_categories' => [
                'label' => 'Quản lý Danh mục',
                'icon'  => 'fa-layer-group',
                'desc'  => 'Thêm, sửa, xóa danh mục',
                'color' => '#2b5ea8',
            ],
            'manage_customers' => [
                'label' => 'Quản lý Khách hàng',
                'icon'  => 'fa-address-card',
                'desc'  => 'Hồ sơ CRM & ghi chú tư vấn',
                'color' => '#5b4b8a',
            ],
            'manage_vouchers' => [
                'label' => 'Quản lý Voucher',
                'icon'  => 'fa-ticket',
                'desc'  => 'Tạo và quản lý mã giảm giá',
                'color' => '#a84c1d',
            ],
            'manage_users' => [
                'label' => 'Quản lý Tài khoản',
                'icon'  => 'fa-user-group',
                'desc'  => 'Xem, thêm, sửa tài khoản nhân sự',
                'color' => '#0f766e',
            ],
            'manage_roles' => [
                'label' => 'Phân quyền Hệ thống',
                'icon'  => 'fa-shield-halved',
                'desc'  => 'Tạo chức vụ & gán quyền',
                'color' => '#c0352b',
            ],
        ];
    }

    public static function label(string $name): array
    {
        return self::meta()[$name] ?? [
            'label' => $name,
            'icon'  => 'fa-key',
            'desc'  => '',
            'color' => '#7a8494',
        ];
    }
}
