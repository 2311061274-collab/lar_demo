@extends('layouts.admin')

@section('title', 'Thêm Ống Kính Mới')

@section('content')
<h1>Thêm Ống Kính Mới</h1>

<p>
    <a href="{{ route('admin.products.index') }}">&larr; Quay lại danh sách ống kính</a>
</p>

<form action="{{ route('admin.products.store') }}" method="POST" enctype="multipart/form-data">
    @csrf

    <div class="form-group">
        <label for="name">Tên Ống Kính: <span style="color:red;">*</span></label>
        <input type="text" id="name" name="name" value="{{ old('name') }}" placeholder="Ví dụ: Sony FE 24-70mm f/2.8 GM II" required>
        @error('name')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="category_id">Danh Mục Phân Loại: <span style="color:red;">*</span></label>
        <select id="category_id" name="category_id" required>
            <option value="">-- Chọn danh mục --</option>
            @foreach ($categories as $cat)
                <option value="{{ $cat->id }}" {{ old('category_id', request('category_id')) == $cat->id ? 'selected' : '' }}>
                    {{ $cat->name }}
                </option>
            @endforeach
        </select>
        @error('category_id')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="sku">Mã SKU / Model:</label>
        <input type="text" id="sku" name="sku" value="{{ old('sku') }}" placeholder="Ví dụ: SEL2470GM2">
        @error('sku')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="focal_length">Tiêu Cự:</label>
        <input type="text" id="focal_length" name="focal_length" value="{{ old('focal_length') }}" placeholder="Ví dụ: 24-70mm, 50mm, 70-200mm">
        @error('focal_length')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="aperture">Khẩu Độ Tối Đa:</label>
        <input type="text" id="aperture" name="aperture" value="{{ old('aperture') }}" placeholder="Ví dụ: f/2.8, f/1.4, f/1.2">
        @error('aperture')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="mount">Ngàm Tương Thích:</label>
        <input type="text" id="mount" name="mount" value="{{ old('mount') }}" placeholder="Ví dụ: Sony E, Canon RF, Nikon Z, Fuji X">
        @error('mount')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="price">Giá Bán (VNĐ): <span style="color:red;">*</span></label>
        <input type="number" id="price" name="price" value="{{ old('price') }}" placeholder="Ví dụ: 49990000" required>
        @error('price')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="stock">Số Lượng Tồn Kho: <span style="color:red;">*</span></label>
        <input type="number" id="stock" name="stock" value="{{ old('stock', 10) }}" required>
        @error('stock')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="status">Trạng Thái: <span style="color:red;">*</span></label>
        <select id="status" name="status" required>
            <option value="in_stock" {{ old('status') == 'in_stock' ? 'selected' : '' }}>Còn hàng</option>
            <option value="out_of_stock" {{ old('status') == 'out_of_stock' ? 'selected' : '' }}>Tạm hết hàng</option>
        </select>
        @error('status')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="image_file">Chọn Ảnh Từ Máy Tính:</label>
        <input type="file" id="image_file" name="image_file" accept="image/*">
        @error('image_file')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="image_url">Hoặc Nhập Link Ảnh Online (URL):</label>
        <input type="url" id="image_url" name="image_url" value="{{ old('image_url') }}" placeholder="https://...">
        @error('image_url')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group">
        <label for="description">Mô Tả Chi Tiết:</label>
        <textarea id="description" name="description" rows="4" placeholder="Nhập thông tin mô tả chi tiết...">{{ old('description') }}</textarea>
        @error('description')
            <div class="error-text">{{ $message }}</div>
        @enderror
    </div>

    <div class="form-group" style="margin-top: 20px;">
        <button type="submit" class="btn">Lưu Ống Kính</button>
        <a href="{{ route('admin.products.index') }}" class="btn btn-secondary">Hủy bỏ</a>
    </div>
</form>
@endsection
