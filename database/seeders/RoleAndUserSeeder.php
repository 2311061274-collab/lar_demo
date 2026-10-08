<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Carbon;
use Spatie\Permission\Models\Role;
use Spatie\Permission\Models\Permission;
use Spatie\Permission\PermissionRegistrar;

class RoleAndUserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Xóa cache quyền của Spatie
        app()[PermissionRegistrar::class]->forgetCachedPermissions();

        // 1. Tạo danh sách các permissions chuẩn
        $permissions = [
            'view_dashboard',
            'manage_orders',
            'manage_products',
            'manage_categories',
            'manage_customers',
            'manage_vouchers',
            'manage_users',
            'view_reports',
            'manage_roles',
            'manage_news',
            'view_news',
            'create_news',
            'edit_news',
            'delete_news',
        ];

        foreach ($permissions as $permissionName) {
            Permission::firstOrCreate(['name' => $permissionName, 'guard_name' => 'web']);
        }

        // 2. Tạo các Roles
        $adminRole = Role::firstOrCreate(['name' => 'admin', 'guard_name' => 'web']);
        $adminRole->syncPermissions(Permission::all());

        $staffRole = Role::firstOrCreate(['name' => 'staff', 'guard_name' => 'web']);
        $staffRole->syncPermissions([
            'view_dashboard',
            'manage_orders',
            'manage_products',
            'manage_categories',
            'view_news',
        ]);

        $customerRole = Role::firstOrCreate(['name' => 'customer', 'guard_name' => 'web']);

        // 3. Tạo tài khoản Admin (admin@example.com / password)
        $admin = User::updateOrCreate(
            ['email' => 'admin@example.com'],
            [
                'name'              => 'Administrator',
                'password'          => Hash::make('password'),
                'role'              => 'admin',
                'email_verified_at' => Carbon::now(),
            ]
        );
        $admin->syncRoles([$adminRole]);

        // 4. Tạo tài khoản User / Khách hàng (user@example.com / password)
        $user = User::updateOrCreate(
            ['email' => 'user@example.com'],
            [
                'name'              => 'Người Dùng Test',
                'password'          => Hash::make('password'),
                'role'              => 'customer',
                'email_verified_at' => Carbon::now(),
            ]
        );
        $user->syncRoles([$customerRole]);

        // 5. Tạo tài khoản Nhân viên (staff@example.com / password)
        $staff = User::updateOrCreate(
            ['email' => 'staff@example.com'],
            [
                'name'              => 'Nhân Viên Bán Hàng',
                'password'          => Hash::make('password'),
                'role'              => 'staff',
                'email_verified_at' => Carbon::now(),
            ]
        );
        $staff->syncRoles([$staffRole]);
    }
}
