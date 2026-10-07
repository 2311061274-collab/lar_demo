<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Database\Factories\UserFactory;
use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use App\Notifications\SendOtpVerification;
use Illuminate\Support\Carbon;
use Spatie\Permission\Traits\HasRoles;

#[Fillable(['name', 'email', 'password', 'role', 'phone', 'cccd', 'birthday', 'address', 'gender', 'verification_code', 'verification_code_expires_at', 'email_verified_at', 'avatar'])]
#[Hidden(['password', 'remember_token'])]
class User extends Authenticatable implements MustVerifyEmail
{
    /** @use HasFactory<UserFactory> */
    use HasFactory, Notifiable, HasRoles;

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at'            => 'datetime',
            'verification_code_expires_at' => 'datetime',
            'password'                     => 'hashed',
        ];
    }

    /**
     * Generate & send OTP to user's email.
     */
    public function sendOtpCode(): void
    {
        $otp = str_pad(random_int(0, 999999), 6, '0', STR_PAD_LEFT);

        $this->update([
            'verification_code'            => $otp,
            'verification_code_expires_at' => Carbon::now()->addMinutes(15),
        ]);

        $this->notify(new SendOtpVerification($otp));
    }

    public function orders()
    {
        return $this->hasMany(Order::class);
    }

    public function carts()
    {
        return $this->hasMany(Cart::class);
    }

    public function sentMessages()
    {
        return $this->hasMany(Message::class, 'sender_id');
    }

    public function receivedMessages()
    {
        return $this->hasMany(Message::class, 'receiver_id');
    }

    public function getAvatarUrlAttribute()
    {
        return $this->avatar ? asset($this->avatar) : null;
    }

    public function addresses()
    {
        return $this->hasMany(UserAddress::class);
    }

    /**
     * Kiểm tra có phải Quản trị viên (Chủ shop) không.
     */
    public function isAdmin(): bool
    {
        return $this->role === 'admin' || $this->hasRole('admin');
    }

    /**
     * Kiểm tra có phải Nhân viên kho & bán hàng không.
     */
    public function isStaff(): bool
    {
        return $this->role === 'staff' || $this->hasRole('staff');
    }

    /**
     * Kiểm tra có phải Khách hàng thông thường không.
     */
    public function isCustomer(): bool
    {
        return $this->role === 'customer' || $this->hasRole('customer');
    }

    /**
     * Kiểm tra có quyền truy cập vào khu vực quản trị kho & hệ thống hay không.
     */
    public function hasAdminAccess(): bool
    {
        if ($this->isAdmin() || $this->isStaff()) {
            return true;
        }

        return $this->can('view_dashboard') 
            || $this->can('manage_products') 
            || $this->can('manage_orders');
    }
}

