import React from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { FiPieChart } from 'react-icons/fi';

// Données fictives pour le graphique en camembert
const orderData = [
  { name: 'Hoàn thành', value: 540, color: '#4ade80' },
  { name: 'Đang xử lý', value: 320, color: '#60a5fa' },
  { name: 'Hủy', value: 88, color: '#f87171' },
];

const COLORS = ['#4ade80', '#60a5fa', '#f87171'];

const OrdersStatistics = () => {
  return (
    <div className="rounded-sm border bg-white text-card-foreground shadow-sm h-full">
      <div className="flex flex-col space-y-1.5 p-6 pb-2">
        <div className="flex items-center gap-2">
          <FiPieChart className="text-blue-500" />
          <h3 className="text-2xl font-semibold leading-none tracking-tight">Thống kê đơn hàng</h3>
        </div>
      </div>
      <div className="p-6 pt-0 flex items-center justify-center min-h-[200px]">
        <ResponsiveContainer width="100%" height={200}>
          <PieChart>
            <Pie
              data={orderData}
              cx="50%"
              cy="50%"
              labelLine={false}
              outerRadius={80}
              fill="#8884d8"
              dataKey="value"
            >
              {orderData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip 
              contentStyle={{ 
                backgroundColor: 'white', 
                borderRadius: '8px', 
                boxShadow: '0 2px 10px rgba(0,0,0,0.1)', 
                border: 'none' 
              }}
            />
            <Legend 
              verticalAlign="bottom" 
              height={36} 
              iconType="circle" 
              layout="horizontal" 
              iconSize={10}
              formatter={(value) => (
                <span className="text-sm font-medium text-gray-700">{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default OrdersStatistics;
