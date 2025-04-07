import TasksOverview from "./TaskOverview"
import OrdersStatistics from "./OrdersStatistics"
import RevenueChart from "./RevenueChart"
import TopProducts from "./TopProducts"
import { ToastContainer } from "react-toastify"

export default function ShopDashboard(){

    return (
        <main className="max-w-7xl mx-auto p-3">
            <div className="dashboard-container grid grid-cols-1 lg:grid-cols-12 gap-6">
            {/* Left column (2/3 width) */}
            <div className="lg:col-span-8 flex flex-col gap-6">
                <TasksOverview />
                <RevenueChart />
            </div>
            
            {/* Right column (1/3 width) */}
            <div className="lg:col-span-4 flex flex-col gap-6">
                <OrdersStatistics />
                <TopProducts />
            </div>
            </div>

            <ToastContainer
                position="bottom-right"
            />
        </main>
    )
}