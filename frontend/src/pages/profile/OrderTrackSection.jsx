import { FaClipboardList, FaDollarSign, FaShippingFast, FaBoxOpen, FaStar } from "react-icons/fa";

const statusMapping = {
    1: { label: "Đặt đơn", icon: <FaClipboardList /> },
    2: { label: "Xác nhận đơn hàng", icon: <FaDollarSign /> },
    4: { label: "Vận chuyển", icon: <FaShippingFast /> },
    5: { label: "Nhận hàng", icon: <FaBoxOpen /> },
    6: { label: "Đánh giá", icon: <FaStar /> }
};

export default function OrderTrackSection({ tracks }){
    return (
        <div className="flex items-center justify-center">
            <div className="flex items-center space-x-4 gap-6">
                {Object.keys(statusMapping).map((key, index) => {
                    const status = parseInt(key);
                    const track = tracks.find(t => t.status === status);
                    const isCompleted = !!track;

                    return (
                        <div key={status} className="flex flex-col items-center gap-2">
                            <div className={`w-18 h-18 flex items-center justify-center rounded-full border-4 ${
                                isCompleted ? "border-green-500 text-green-500" : "border-gray-300 text-gray-400"
                            }`}>
                                {
                                    <div className="text-3xl">
                                        {statusMapping[status].icon}
                                    </div>
                                }
                            </div>
                            <p className="text-center text-l">{statusMapping[status].label}</p>
                            {track && (
                                <p className="text-xs text-gray-500">
                                    {new Date(track.updatedAt).toLocaleString()}
                                </p>
                            )}
                            {/* {index < Object.keys(statusMapping).length - 1 && ( */}
                                <div className={`h-1 w-12 ${isCompleted ? "bg-green-500" : "bg-gray-300"}`} />
                            {/* )} */}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};