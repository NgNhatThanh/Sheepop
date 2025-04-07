import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom'
import { fetchWithAuth } from '../../../util/AuthUtil'
import { BASE_API_URL } from '../../../constants';
import { toast } from 'react-toastify';

const TasksOverview = () => {

  const [taskCategories, setTaskCategories] = useState([
    { name: "Đơn cần xử lý", color: "bg-blue-100 text-blue-600", direct: "../order-list?type=1", count: 0 },
    { name: "Đơn chờ vận chuyển", color: "bg-amber-100 text-amber-600", direct: "../order-list?type=2", count: 0 },
    { name: "Sản phẩm bị đình chỉ", color: "bg-red-100 text-red-600", direct: "../product-list?type=1", count: 0 },
  ]);

  useEffect(() => {
    
    const fetchTaskOverview = () => {
      fetchWithAuth(`${BASE_API_URL}/v1/shop/dashboard/get_task_overview`)
        .then(res => res.json())
        .then(res => {
          if(res.message){
            toast.error("Không thể thực hiện")
          }
          else{
            const data = [res.pendingOrders, res.preparingOrders, res.restrictedProducts]
            setTaskCategories(prev =>
              prev.map((item, index) => ({
                ...item,
                count: data[index] || 0,
              }))
            );
          }
        })
        .catch(e => {
          console.log(e)
          toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
        })
    }

    fetchTaskOverview()
  }, [])

  return (
    <div className="rounded-sm border bg-white text-gray-800 shadow-sm">
      <div className="flex flex-col space-y-1.5 p-6 pb-2">
        <h3 className="text-2xl font-semibold leading-none tracking-tight">Việc cần làm</h3>
      </div>
      <div className="p-6 pt-0">
        <div className="flex flex-wrap gap-4 justify-between">
          {taskCategories.map((category, index) => (
            <Link 
              key={index} 
              to={category.direct}
              className="cursor-pointer flex flex-col items-center p-4 rounded-lg min-w-24 transition-all duration-300 hover:bg-gray-300 hover:scale-105"
            >
              <span className={`text-2xl font-bold mb-2 ${category.color.split(' ')[1]}`}>
                {category.count}
              </span>
              <span className="text-center text-sm text-gray-600">
                {category.name}
              </span>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TasksOverview;
