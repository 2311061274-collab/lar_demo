<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Illuminate\Support\Facades\Auth;

class AdminAccessMiddleware
{
    /**
     * Handle an incoming request.
     * Đảm bảo chỉ có Admin hoặc Nhân viên (Staff) có quyền hạn mới được vào khu vực quản trị kho & hệ thống.
     * Người dùng (Khách hàng) sẽ bị chặn triệt để.
     */
    public function handle(Request $request, Closure $next): Response
    {
        if (!Auth::check()) {
            if ($request->expectsJson() || $request->is('admin/chat/*') || $request->is('api/*')) {
                return response()->json([
                    'success' => false,
                    'message' => 'Vui lòng đăng nhập để thực hiện tác vụ này.'
                ], 401);
            }

            return redirect()->route('login')->with('error', 'Vui lòng đăng nhập bằng tài khoản quản trị.');
        }

        $user = Auth::user();

        // Kiểm tra quyền hạn quản trị
        if (!$user->hasAdminAccess()) {
            if ($request->expectsJson() || $request->is('admin/chat/*') || $request->is('api/*')) {
                return response()->json([
                    'success' => false,
                    'message' => 'Từ chối truy cập: Bạn không có quyền truy cập vào khu vực quản trị kho & hệ thống.'
                ], 403);
            }

            return redirect()->route('storefront.index')
                ->with('error', 'Từ chối truy cập: Bạn không có quyền hạn quản trị viên để vào khu vực quản trị kho & hệ thống!');
        }

        return $next($request);
    }
}
