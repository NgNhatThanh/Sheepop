import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { FiTrendingUp } from 'react-icons/fi';

// Données fictives pour le graphique de revenus
const revenueData = [
  { name: 'T1', value: 4000 },
  { name: 'T2', value: 3000 },
  { name: 'T3', value: 5000 },
  { name: 'T4', value: 2780 },
  { name: 'T5', value: 1890 },
  { name: 'T6', value: 2390 },
  { name: 'T7', value: 3490 },
  { name: 'T8', value: 4000 },
  { name: 'T9', value: 2400 },
  { name: 'T10', value: 2400 },
  { name: 'T11', value: 3200 },
  { name: 'T12', value: 5800 },
];

const RevenueChart = () => {
  return (
    <div className="rounded-sm border bg-white text-card-foreground shadow-sm">
      <div className="flex flex-col space-y-1.5 p-6 pb-2">
        <div className="flex items-center gap-2">
          <FiTrendingUp className="text-blue-500" />
          <h3 className="text-2xl font-semibold leading-none tracking-tight">Doanh thu</h3>
        </div>
      </div>
      <div className="p-6 pt-0 min-h-[260px]">
        <ResponsiveContainer width="100%" height={250}>
          <LineChart
            data={revenueData}
            margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
          >
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
            <XAxis dataKey="name" axisLine={false} tickLine={false} />
            <YAxis axisLine={false} tickLine={false} />
            <Tooltip 
              contentStyle={{ backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', border: 'none' }}
              labelStyle={{ fontSize: '12px', fontWeight: 'bold', color: '#666' }}
            />
            <Line 
              type="monotone" 
              dataKey="value" 
              stroke="#3498db" 
              strokeWidth={3} 
              dot={{ r: 0 }} 
              activeDot={{ r: 6, fill: '#3498db', strokeWidth: 0 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default RevenueChart;
