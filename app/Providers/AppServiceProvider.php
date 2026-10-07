<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Pagination\Paginator;
use Illuminate\Support\Facades\URL;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        // Bắt buộc toàn bộ URL, link asset và action của form sử dụng HTTPS trên production
        if ($this->app->environment('production')) {
            URL::forceScheme('https');
        }

        // Admin pagination là mặc định; các trang storefront gọi ->links('vendor.pagination.storefront') rõ ràng
        Paginator::defaultView('vendor.pagination.admin');
        Paginator::defaultSimpleView('vendor.pagination.admin');
    }
}