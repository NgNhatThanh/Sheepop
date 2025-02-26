export default function Footer(){
    return(
        <div className="footer-container bg-gray-800 text-white py-6 mt-auto">
            <div className="container mx-auto grid grid-cols-3 gap-4 text-sm">
            <div>
                <h3 className="font-bold mb-2">Về chúng tôi</h3>
                <p>Giới thiệu Shopee</p>
                <p>Tuyển dụng</p>
                <p>Điều khoản</p>
            </div>
            <div>
                <h3 className="font-bold mb-2">Hỗ trợ khách hàng</h3>
                <p>Trung tâm hỗ trợ</p>
                <p>Hướng dẫn mua hàng</p>
                <p>Chính sách bảo mật</p>
            </div>
                <div>
                    <h3 className="font-bold mb-2">Liên hệ</h3>
                    <p>Email: support@shopee.vn</p>
                    <p>Hotline: 1800 1234</p>
                    <p>Địa chỉ: TP. Hồ Chí Minh, Việt Nam</p>
                </div>
            </div>
        </div>
    )
}