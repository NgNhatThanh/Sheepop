import { useEffect, useState } from "react"
import Pagination from '../../common/Pagination'
import { fetchWithAuth } from '../../../util/AuthUtil'
import { BASE_API_URL } from "../../../constants"
import { ToastContainer, toast } from "react-toastify"

export default function CategoryPage(){

    const [categories, setCategories] = useState([])
    const [page, setPage] = useState(1)
    const [limit, setLimit] = useState(10)
    const [maxPage, setMaxPage] = useState(1)
    const [sortType, setSortType] = useState(1)
    const [keyword, setKeyword] = useState("")

    const fetchCategories = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/admin/category/get_list
            ?page=${page-1}&limit=${limit}&sortType=${sortType}&keyword=${keyword}`.replace(/\s+/g, ""))
            .then(res => res.json())
            .then(res => {
                console.log(res)
                setCategories(res.content)
                setMaxPage(res.totalPages)
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau"))

    }

    useEffect(() => {
        fetchCategories()
    }, [page, limit, sortType, keyword])

    return(
        <div className="bg-white h-100">
            <div className="bg-white p-4 shadow-xs">
                <div className="flex justify-between items-center gap-4">
                    <input
                        type="text"
                        placeholder="Tìm kiếm danh mục..."
                        className="border border-gray-300 rounded-lg p-2 w-1/3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    
                    <div className="flex gap-2 items-center">
                        <select className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                            <option value="name-desc">Số lượng sản phẩm (giảm dần)</option>
                            <option value="name-asc">Số lượng sản phẩm (tăng dần)</option>
                            <option value="date-new">Ngày thêm vào (mới nhất)</option>
                            <option value="date-old">Ngày thêm vào (cũ nhất)</option>
                        </select>
                    
                        <button className="cursor-pointer bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600">
                            + Thêm danh mục lớn
                        </button>
                    </div>
                </div>
            </div>

            <div className="mt-4">
                <table className="w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className="bg-gray-100">
                            <th className="border border-gray-300 p-2 w-1/2">Tên</th>
                            <th className="border border-gray-300 p-2 w-1/4">Mô tả</th>
                            <th className="border border-gray-300 p-2">Số lượng sản phẩm</th>
                            <th className="border border-gray-300 p-2">Thêm vào ngày</th>
                            <th className="border border-gray-300 p-2">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {/* Dữ liệu mẫu */}
                        <tr>
                        <td className="border border-gray-300 p-2">Danh mục 1</td>
                        <td className="border border-gray-300 p-2">Mô tả danh mục 1</td>
                        <td className="border border-gray-300 p-2 text-center">10</td>
                        <td className="border border-gray-300 p-2 text-center">2025-03-01</td>
                        <td className="border border-gray-300 p-2 text-center">
                            <button className="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600">Xóa</button>
                        </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <Pagination
                page={page}
                limit={limit}
                setPage={setPage}
                setLimit={setLimit}
                maxPage={maxPage}
            />
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}