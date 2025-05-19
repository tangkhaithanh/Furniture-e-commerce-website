
// Hàm cập nhật số lượng sản phẩm trong giỏ hàng
function updateCartCount() {
    fetch('/api/cart/count')
        .then(response => response.json())
        .then(count => {
            const cartBadge = document.getElementById('cartItemCount');
            if (count > 0) {
                cartBadge.textContent = count;
                cartBadge.style.display = 'flex';
            } else {
                cartBadge.style.display = 'none';
            }
        })
        .catch(error => console.error('Error fetching cart count:', error));
}

// Thêm style cho cart badge
document.head.insertAdjacentHTML('beforeend', `
    <style>
        .cart-badge {
            position: absolute;
            top: -8px;
            right: -8px;
            background-color: #ff4444;
            color: white;
            border-radius: 50%;
            min-width: 18px;
            height: 18px;
            font-size: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2px;
            font-weight: bold;
        }
        
        .cart-button {
            position: relative;
            text-decoration: none;
            color: inherit;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .cart-button:hover {
            color: #ffc107;
        }
        
        .cart-button i {
            font-size: 1.2rem;
        }
    </style>
`);

// Cập nhật số lượng khi trang được tải
document.addEventListener('DOMContentLoaded', updateCartCount);

// Cập nhật số lượng mỗi khi có thay đổi trong giỏ hàng
// Bạn có thể gọi updateCartCount() sau khi thêm/xóa sản phẩm
// Ví dụ: Cập nhật mỗi 30 giây
setInterval(updateCartCount, 30000);
// Hàm lấy productId từ URL
        function getProductIdFromUrl() {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get('productId');
        }
        function updateCartDropdown() {
            fetch('/api/cart/getCartItems')
                .then(response => response.json())
                .then(data => {
                    const cartItemsList = document.getElementById('cartItemsList');
                    
                    if (data.cartItems && data.cartItems.length > 0) {
                        let html = '';
                        data.cartItems.forEach(item => {
							// Định dạng giá trị giá sản phẩm
		                    const formattedPrice = parseFloat(item.product.price).toLocaleString('vi-VN', {
		                        minimumFractionDigits: 0,
		                        maximumFractionDigits: 0
		                    });
												
                            html += `
                                <div class="cart-item">
                                    <img src="${item.product.imageUrl}" alt="${item.product.name}">
                                    <div class="cart-item-info">
                                        <div class="cart-item-title">${item.product.name}</div>
                                        <div class="cart-item-price">${formattedPrice} VNĐ</div>
                                        <div class="cart-item-quantity">Số lượng: ${item.quantity}</div>
                                    </div>
                                </div>
                            `;
                        });
                        
                        cartItemsList.innerHTML = html;
                    } else {
                        cartItemsList.innerHTML = '<div class="cart-empty">Giỏ hàng trống</div>';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    const cartItemsList = document.getElementById('cartItemsList');
                    cartItemsList.innerHTML = '<div class="cart-empty text-danger">Có lỗi xảy ra khi tải giỏ hàng</div>';
                });
        }

        // Thêm hàm format tiền tệ nếu chưa có
        function formatPrice(price) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(price);
        }

        // Đảm bảo cập nhật khi tải trang và khi hover
        document.addEventListener('DOMContentLoaded', updateCartDropdown);
        document.querySelector('.cart-wrapper').addEventListener('mouseenter', updateCartDropdown);