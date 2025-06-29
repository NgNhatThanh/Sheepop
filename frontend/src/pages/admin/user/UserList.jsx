import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { fetchWithAuth } from '../../../util/AuthUtil'
import { BASE_API_URL } from "../../../constants";
import { FaChevronDown } from "react-icons/fa";
import { ToastContainer, toast } from "react-toastify";
import Pagination from "../../common/Pagination";
import { FaUserEdit } from "react-icons/fa";
import DeleteUserForm from "./DeleteUserForm";
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { use } from "react";

const tabs = [
    "Đang hoạt động",
    "Đã xóa"
];

const filters = [
    "Tất cả",
    "Họ tên",
    "Username",
    "Số điện thoại",
    "Email"
];

export default function UserList(){

    const navigate = useNavigate()
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const [isLoading, setIsLoading] = useState(false)
    const [limit, setLimit] = useState(10)
    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(1)
    const [totalUsers, setTotalUsers] = useState(0)
    const [isEmpty, setIsEmpty] = useState(false)
    const currentType = parseInt(searchParams.get("type")) || 0;
    const [users, setUsers] = useState([])

    const [filterType, setFilterType] = useState(0);
    const [keyword, setKeyword] = useState('')
    const [searchQuery, setSearchQuery] = useState("");
    const [openFilter, setOpenFilter] = useState(false);
    const [sortType, setSortType] = useState(0)

    const [deleteUserId, setDeleteUserId] = useState(null)
    const [restoreUserId, setRestoreUserId] = useState(null)

    const resetFilter = () => {
        setSearchQuery("");
        setFilterType(0)
        setKeyword("")
        setPage(1)
    }

    const sortIcons = (t1, t2) => {
        return (
            <div>
                <p className={`${sortType === t1 ? 'text-gray-800' : 'text-gray-300'}`}>⏶</p>
                <p className={`${sortType === t2 ? 'text-gray-800' : 'text-gray-300'}`}>⏷</p>
            </div>
        )
    }

    const onSuccessDeleteUser = () => {
        toast.success("Xóa người dùng thành công")
        setUsers(prev => prev.filter(cur => cur.id !== deleteUserId))
        setDeleteUserId(null)
    }

    const fetchUsers = () => {
        setIsEmpty(false)
        setIsLoading(true)
        fetchWithAuth(`${BASE_API_URL}/v1/admin/user/get_list?type=${currentType}&filterType=${filterType}&keyword=${keyword}&sortType=${sortType}&page=${page - 1}&limit=${limit}`, window.location, true)
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error(res.message)
                }
                else{
                    if(res.content.length === 0) setIsEmpty(true)
                    setTotalUsers(res.totalElements)
                    setTotalPage(res.totalPages)
                    setUsers(res.content)
                }
            })
            .finally(() => setIsLoading(false))
    }

    const handleRestoreUser = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/admin/user/restore_user/${restoreUserId}`, "/", true, {
            method: "POST"
        })
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error(res.message)
                }
                else{
                    toast.success("Khôi phục người dùng thành công")
                    setUsers(prev => prev.filter(cur => cur.id !== restoreUserId))
                    setRestoreUserId(null)
                }
            })
            .catch(e => {
                console.log("Error: ", e)
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
            })
    }

    useEffect(() => {
        fetchUsers()
    }, [page, limit, keyword, currentType, sortType])

    return (
        <div>
            <div className="w-full bg-white shadow-md sticky top-12 z-1 flex justify-center rounded-sm">
                {tabs.map((tab, index) => (
                <button
                    key={index}
                    className={`cursor-pointer px-4 py-2 ${
                    currentType === index
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-900"
                    } hover:text-blue-500`}
                    onClick={() => navigate(`/admin/user?type=${index}`)}
                >
                    {tab}
                </button>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-3 bg-white p-4 rounded mt-4 shadow-md">
                <div className="relative">
                    <button
                        onClick={() => setOpenFilter(!openFilter)}
                        className="flex cursor-pointer justify-between border border-gray-300 transtion-all duration-200 px-3 py-2 rounded flex items-center gap-2 w-44 hover:border-blue-500"
                    >
                    {filters[filterType]}
                    <FaChevronDown className={`transition-transform ${openFilter ? "rotate-180" : "rotate-0"}`} />
                    </button>
                    {openFilter && (
                    <ul className="absolute left-0 top-full mt-1 w-52 bg-white border border-gray-300 rounded shadow-lg overflow-hidden z-10">
                        {filters.map((item, index) => (
                        <li
                            key={item}
                            className={`px-3 py-2 cursor-pointer ${
                                item === filters[filterType] ? "text-blue-500 font-semibold" : "text-gray-700"
                            } hover:bg-gray-100`}
                            onClick={() => {
                                setSearchQuery("")
                                setFilterType(index);
                                setOpenFilter(false);
                            }}
                        >
                            {item}
                        </li>
                        ))}
                    </ul>
                    )}
                </div>

                <input
                    type="text"
                    disabled={filterType === 0}
                    placeholder={filterType === 0 ? 'Tìm kiếm...' : `Tìm theo ${filters[filterType].toLowerCase()}...`}
                    className={`border border-gray-300 px-3 py-2 rounded w-60 ${filterType === 0 && 'bg-gray-100 cursor-not-allowed'}`}
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        setFilterType(filterType)
                        setKeyword(searchQuery)
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        resetFilter()
                    }}
                >
                    Đặt lại
                </button>
            </div>

            <p className="text-xl mt-4 font-semibold">{totalUsers} người dùng</p>

            <div className="bg-white mt-2">
                <table className="table-fixed w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className=" w-full">
                            <th className="border border-gray-300 p-2">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => setSortType(sortType === 1 ? 0 : 1)}
                                >
                                    Ngày tham gia
                                    {sortIcons(1, 0)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2">Họ tên</th>
                            <th className="border border-gray-300 p-2">Username</th>
                            <th className="border border-gray-300 p-2">SĐT</th>
                            <th className="border border-gray-300 p-2">Email</th>
                            <th className="border border-gray-300 p-2 w-1/12">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <>
                                <tr key={user.id} className="border-b border-gray-300">
                                    <td className="text-center border-r border-gray-300">
                                        {new Date(user.createdAt).toLocaleDateString()}
                                    </td>
                                    <td className="items-center border-r border-gray-300">
                                        <div className="p-5 flex gap-1">
                                            <img src={user.avatarUrl} className="w-20 h-20"/>
                                            <p>{user.fullName}</p>
                                        </div>
                                    </td>
                                    <td className="text-center border-r border-gray-300">
                                        <p>
                                            {user.username}
                                        </p> 
                                    </td>
                                    <td className="text-center border-r border-gray-300">
                                        <p>{user.phoneNumber}</p>
                                    </td>
                                    <td className="text-center border-r border-gray-300">
                                        <p>{user.email}</p>
                                    </td>
                                    <td>
                                        <div className="flex flex-col gap-2 justify-center p-2">
                                            {!user.deleted && (
                                                <button 
                                                    className="text-white text-center cursor-pointer rounded-xs bg-blue-400 p-1 hover:bg-blue-500"
                                                    onClick={() => {}}
                                                >
                                                    Xem shop
                                                </button>
                                            )}

                                            {!user.deleted && (
                                                <button 
                                                    className="text-white text-center cursor-pointer rounded-xs bg-red-400 p-1 hover:bg-red-500"
                                                    onClick={() => setDeleteUserId(user.id)}
                                                >
                                                    Xóa
                                                </button>
                                            )} 

                                            {user.deleted && (
                                                <button 
                                                    className="text-white text-center cursor-pointer rounded-xs bg-green-400 p-1 hover:bg-green-500"
                                                    onClick={() => setRestoreUserId(user.id)}
                                                >
                                                    Khôi phục
                                                </button>
                                            )} 
                                        </div>
                                    </td>
                                </tr>
                                {user.deleted && (
                                    <tr>
                                        <td colSpan={6} className="text-center font-semibold text-red-500 p-2">
                                            Lí do xóa: {user.deleteReason}
                                        </td>
                                    </tr>
                                )}
                            </>
                        ))}

                        
                    </tbody>
                </table>
                {users.length > 0 && (
                    <Pagination
                        page={page}
                        setPage={setPage}
                        limit={limit}
                        setLimit={setLimit}
                        maxPage={totalPage}
                    />
                )} 
            </div>

            <Dialog
                open={restoreUserId !== null}
                onClose={() => setRestoreUserId(null)}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"Khôi phục người dùng?"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Người dùng sẽ được phép đăng bán, mua hàng, và các sản phẩm của người dùng
                        cũng sẽ được khôi phục
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setRestoreUserId(null)}>Hủy</Button>
                    <Button onClick={() => {
                        handleRestoreUser()
                    }} autoFocus>
                        Đồng ý
                    </Button>
                </DialogActions>
            </Dialog>

            {isLoading && (
                <div role="status" className="flex justify-center mt-2">
                    <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/>
                        <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/>
                    </svg>
                    <span className="sr-only">Loading...</span>
                </div>
            )}

            {isEmpty && (
                <div className="w-full bg-white shadow-md rounded-sm h-100 mt-3 flex items-center justify-center">
                    <div className="text-center">
                    <FaUserEdit className="text-blue-300 text-8xl mx-auto mb-2"/>
                    <p className="text-xl">Không có người dùng nào</p>
                    </div>
                </div>
            )}

            {deleteUserId && (
                <DeleteUserForm
                    userId={deleteUserId}
                    closeForm={() => setDeleteUserId(null)}
                    onSuccess={onSuccessDeleteUser}
                />
            )}

            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}