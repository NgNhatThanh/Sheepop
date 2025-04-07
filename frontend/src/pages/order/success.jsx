import { Button } from "@mui/material";
import { FaCheckCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function SuccessPage() {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-lg text-center max-w-md">
        <FaCheckCircle className="text-green-500 w-16 h-16 mx-auto" fontSize="large" />
        <h2 className="text-2xl font-bold mt-4">Đặt hàng thành công!</h2>
        <p className="text-gray-600 mt-2">Cảm ơn bạn đã mua sắm với chúng tôi. Đơn hàng của bạn sẽ sớm được xử lý.</p>
        <div className="mt-6 flex gap-4">
          <Button onClick={() => navigate("/")} variant="contained" color="primary" fullWidth>
            Tiếp tục mua hàng
          </Button>
          <Button onClick={() => navigate("/account/orders")} variant="contained" color="secondary" fullWidth>
            Xem các đơn hàng
          </Button>
        </div>
      </div>
    </div>
  );
  
}
