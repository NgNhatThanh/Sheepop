import { useLocation, Link, useNavigate } from "react-router-dom";
import React from "react";
import { useState, useEffect } from "react";
import { fetchWithAuth } from '../../../util/AuthUtil'
import { formatDate } from '../../../util/DateUtil'
import { BASE_API_URL } from "../../../constants";
import Pagination from "../../common/Pagination";
import { ToastContainer, toast } from "react-toastify";
import { LuPackageX } from "react-icons/lu";
import TableLoading from '../../common/TableLoading'
import Modal from '../../common/Modal'

const tabs = [
    "Đang kích hoạt",
    "Vi phạm",
    "Chờ kiểm duyệt",
    "Chưa được đăng",
    "Hết hàng"
];

export default function ShopProducts(){

    const navigate = useNavigate()
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const currentType = parseInt(searchParams.get("type")) || 0;

    const [changeVisibleProductId, setChangeVisibleProductId] = useState(null)
    const [deleteProductId, setDeleteProductId] = useState(null)
    const [openOtherActionDropdowm, setOpenOtherActionDropdown] = useState({})
    const [page, setPage] = useState(1)
    const [limit, setLimit] = useState(10)
    const [products, setProducts] = useState([]);
    const [totalPages, setTotalPages] = useState(1)
    const [totalProducts, setTotalProducts] = useState(0)
    const [isLoading, setIsLoading] = useState(false)
    const [isShowingSKUList, setIsShowingSKUList] = useState({})

    const [keyword, setKeyword] = useState("")
    const [categoryId, setCategoryId] = useState("")
    const [sortType, setSortType] = useState(0)

    const [searchInputKeyword, setSearchInputKeyword] = useState("")
    const [selectedSearchCategory, setSelectedCategory] = useState(null)

    const [shopCategories, setShopCategories] = useState([])
    const [openOpenCategoriesDropdown, setOpenCategoriesDropdown] = useState(false)
    
    const loadProduct = async () => {
        setIsLoading(true)
        const res = await fetchWithAuth(`${BASE_API_URL}/v1/shop/product/list?type=${currentType}&sortType=${sortType}&keyword=${keyword}&categoryId=${categoryId}&page=${page - 1}&limit=${limit}`)
        await res.json()
            .then(res => {
                console.log(products)
                if(res.message){
                    toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
                }
                else{
                    setTotalPages(res.totalPages)
                    setTotalProducts(res.totalElements)
                    setProducts(res.content)
                }
            })
        setIsLoading(false)
    }
    
    const toggleShowSKUList = (productId) => {
        setIsShowingSKUList((prev) => ({
        ...prev,
        [productId]: !prev[productId]
        }))
    }

    const changeProductVisible = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/product/change_visible?productId=${productId}`, window.location, true, {
            method: "POST"
        })
            .then(res => {
                if(!res.ok){
                    toast.error('Có lỗi xảy ra, vui lòng thử lại sau!')
                    return
                }
                const updateProd = [...products]
                updateProd.map(prod => {
                    if(prod.id === productId){
                        prod.visible = !prod.visible
                    }
                    return prod
                })
                setProducts(updateProd)
            })
            .catch(() => toast.error('Có lỗi xảy ra, vui lòng thử lại sau!'))
    }

    const handleDeleteProduct = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/product/delete/${productId}`, window.location, true, {
            method: "POST"
        })
            .then(res => {
                if(!res.ok){
                    toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
                    return
                }
                toast.success("Xóa sản phẩm thành công")
                setProducts(prev => prev.filter(prod => prod.id !== productId))
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau!"))
    }
    
    useEffect(() => {
        loadProduct()
    }, [currentType, page, limit, sortType, keyword, categoryId])

    useEffect(() => {   
        const fetchShopCategory = () => {
            fetchWithAuth(`${BASE_API_URL}/v1/shop/product/get_categories`)
                .then(res => res.json())
                .then(res => setShopCategories(res))
        }

        fetchShopCategory()
    }, [])

    const displayCategory = (cat) => {
        let disp = cat.name
        cat = cat.parent
        while(cat){
            disp += ' > ' + cat.name 
            cat = cat.parent
        }
        return disp
    }

    const sortIcons = (t1, t2) => {
        return (
            <div>
                <p className={`${sortType === t1 ? 'text-gray-800' : 'text-gray-300'}`}>⏶</p>
                <p className={`${sortType === t2 ? 'text-gray-800' : 'text-gray-300'}`}>⏷</p>
            </div>
        )
    }
    
    return (
        <div>
            <div className="flex justify-between mb-5">
                <h2 className="text-2xl font-bold">Sản phẩm</h2>
                <Link 
                    to="../add-product"
                    className='p-2 bg-blue-500 rounded-sm text-white font-semibold text-l cursor-pointer hover:bg-blue-600'
                >
                    + Thêm sản phẩm
                </Link>
                
            </div>

            <div className="w-full bg-white shadow-md sticky top-12 z-1 flex justify-center rounded-sm">
                {tabs.map((tab, index) => (
                <button
                    key={index}
                    className={`cursor-pointer px-4 py-2 ${
                    currentType === index
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-900"
                    } hover:text-blue-500`}
                    onClick={() => navigate(`/myshop/product-list?type=${index}`)}
                >
                    {tab}
                </button>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-3 bg-white p-4 rounded mt-4 shadow-md">
                <input
                    value={searchInputKeyword}
                    type="text"
                    placeholder="Tìm tên sản phẩm"
                    onChange={e => setSearchInputKeyword(e.target.value)}
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <div className="relative">
                    <button
                        type="text"
                        placeholder="Tìm theo ngành hàng"
                        className={`border border-gray-300 px-3 py-2 rounded min-w-60 flex gap-2 justify-between items-center transtion-all duration-200 cursor-pointer hover:border-blue-600`}
                        onClick={() => setOpenCategoriesDropdown(!openOpenCategoriesDropdown)}
                    >
                        {selectedSearchCategory ? displayCategory(selectedSearchCategory) : "Tất cả ngành hàng"}
                        <span className={`transform transition-transform ${!openOpenCategoriesDropdown ? "rotate-180" : "rotate-0"}`}>
                            ▲
                        </span>
                    </button>

                    <ul
                        className={`absolute right-0 top-full mb-1 w-full bg-white border border-gray-300 rounded shadow-md overflow-hidden transition-all duration-300 ${
                            openOpenCategoriesDropdown ? "opacity-100 max-h-40" : "opacity-0 max-h-0"
                        }`}
                    >
                         <li
                            className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                            onClick={() => {
                                setSelectedCategory(null)
                                setOpenCategoriesDropdown(false);
                            }}
                        >
                            Tất cả
                        </li>
                        {shopCategories.length > 0 && shopCategories.map((cat, index) => (
                            <li
                                key={index}
                                className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                                onClick={() => {
                                    setSelectedCategory(cat);
                                    setOpenCategoriesDropdown(false);
                                }}
                            >
                                {displayCategory(cat)}
                            </li>
                        ))}
                    </ul>
                </div>

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        setKeyword(searchInputKeyword)
                        setCategoryId(selectedSearchCategory ? selectedSearchCategory.id : "")
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        setSelectedCategory(null)
                        setSearchInputKeyword("")
                        setKeyword("")
                        setCategoryId("")
                    }}
                >
                    Đặt lại
                </button>
            </div>

            <p className="text-xl font-semibold mb-2 mt-4">{totalProducts} sản phẩm</p>
            <div className="container m-auto bg-white rounded-sm ">
                <table className="table-auto w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className="bg-white w-full">
                            <th className="border border-gray-300 p-2 w-fit">STT</th>
                            <th className="border border-gray-300 p-2 w-3/7">Tên</th>
                            <th className="border border-gray-300 p-2 w-1/11">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => {
                                        setSortType(sortType === 5 ? 4 : 5)
                                    }}
                                >
                                    Giá
                                    {sortIcons(5, 4)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2 w-1/11">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => {
                                        setSortType(sortType === 3 ? 2 : 3)
                                    }}
                                >
                                    Kho hàng
                                    {sortIcons(3, 2)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2 w-1/11">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => {
                                        setSortType(sortType === 1 ? 0 : 1)
                                    }}
                                >
                                    Doanh số
                                    {sortIcons(1, 0)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2 w-1/11">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => {
                                        setSortType(sortType === 9 ? 8 : 9)
                                    }}
                                >
                                    Đã bán
                                    {sortIcons(9, 8)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2 w-1/11">
                                <div 
                                    className="cursor-pointer flex gap-2 justify-center items-center"
                                    onClick={() => {
                                        setSortType(sortType === 7 ? 6 : 7)
                                    }}
                                >
                                    Thời gian tạo
                                    {sortIcons(7, 6)}
                                </div>
                            </th>
                            <th className="border border-gray-300 p-2">Thao tác</th>
                        </tr>
                    </thead>

                    <tbody>
                    {products.map((product, index) => (
                        <React.Fragment key={product.id}>
                        <tr id={product.id}>
                            <td className="border border-gray-300 p-2 text-center">{index + 1}</td>
                            <td className="border border-gray-300 p-2 text-left">
                                {!product.visible && (
                                    <p 
                                        className="text-gray-600 w-fit text-sm p-1 bg-gray-200 font-semibold rounded-l mb-2"
                                    >
                                        Đã ẩn
                                    </p> 
                                )}  
                                <div className="flex justify-start gap-3">
                                    <img
                                    src={product.thumbnailUrl}
                                    alt={product.name}
                                    className="w-12 h-12 object-cover"
                                    />
                                    <p className="mr-auto">{product.name}</p>
                                </div>
                                
                                {product.restricted && (
                                    <div className="flex gap-1">
                                        <p className="text-red-500 font-semibold">Đình chỉ: </p>
                                        <p>{product.restrictReason}</p>
                                    </div>
                                )}
                            </td>
                            <td className="border border-gray-300 p-2 text-center">{product.price.toLocaleString()} VND</td>
                            <td className="border border-gray-300 p-2 text-center">{product.quantity}</td>
                            <td className="border border-gray-300 p-2 text-center">{product.revenue.toLocaleString()} VND</td>
                            <td className="border border-gray-300 p-2 text-center">{product.sold}</td>
                            <td className="border border-gray-300 p-2 text-center">{formatDate(product.createdAt)}</td>
                            <td className="border border-gray-300 p-2">
                            <div className="flex flex-col gap-1">
                                <Link 
                                    to={`../product/${product.id}`}
                                    className="text-white text-center cursor-pointer rounded-xs bg-blue-400 p-1 hover:bg-blue-500"
                                >
                                    Chỉnh sửa
                                </Link>
                                {product.skuList.length > 0 && 
                                <button
                                    className="cursor-pointer rounded-xs border-1 border-gray-700 p-1 hover:bg-gray-100"
                                    onClick={() => toggleShowSKUList(product.id)}
                                >
                                    {isShowingSKUList[product.id] ? "Thu gọn" : "Mở rộng"}  
                                </button>}

                                <div className="relative">
                                    <p 
                                        className="text-blue-500 cursor-pointer text-center"
                                        onClick={() => setOpenOtherActionDropdown(prev => ({
                                            ...prev,
                                            [index]: openOtherActionDropdowm[index] ? false : true
                                        }))} 
                                    >
                                        Khác ▼
                                    </p>

                                    {openOtherActionDropdowm[index] && (
                                        <ul
                                            className={`opacity-100 absolute right-0 z-10 top-full mb-1 w-24 bg-white border border-gray-300 rounded shadow-md overflow-hidden transition-all duration-300 max-h-40`}
                                        >
                                            <li
                                                className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                                                onClick={() => {
                                                    window.open(`/preview/${product.id}`, '_blank')
                                                }}
                                            >
                                                Xem trước
                                            </li>

                                            <li
                                                className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                                                onClick={() => setChangeVisibleProductId(product.id)}
                                            >
                                                {product.visible ? 'Ẩn' : 'Hiện'}
                                            </li>

                                            <li
                                                className="px-2 py-1 cursor-pointer text-red-500 hover:bg-gray-200"
                                                onClick={() => setDeleteProductId(product.id)}   
                                            >
                                                Xóa
                                            </li>
                                        </ul>
                                    )}
                                </div>

                                
                            </div>
                            </td>
                        </tr>
                        {isShowingSKUList[product.id] && (
                            product.skuList.map(skuProduct => (
                            <tr>
                            <td></td>
                            <td className="border border-gray-300 p-2 text-center">
                                {skuProduct.attributes.map(attribute => (
                                <p>{attribute.name}: {attribute.value}</p>
                                ))}
                                SKU: {skuProduct.sku}
                            </td>
                            <td className="border border-gray-300 p-2 text-center">{skuProduct.price.toLocaleString()} VND</td>
                            <td className="border border-gray-300 p-2 text-center">{skuProduct.quantity}</td>
                            </tr>
                            ))
                        )}

                        <Modal
                            open={changeVisibleProductId !== null}
                            title='Thay đổi hiển thị sản phẩm?'
                            content='Người mua sẽ không nhìn thấy và mua được sản phẩm của bạn nếu bạn ẩn đi,
                                    và ngược lại'
                            onclose={() => setChangeVisibleProductId(null)}
                            onSucess={() => {
                                changeProductVisible(changeVisibleProductId)
                                setChangeVisibleProductId(null)
                            }}
                        />

                        <Modal
                            open={deleteProductId !== null}
                            title='Xóa sản phẩm?'
                            content='Thao tác này sẽ xóa hoàn toàn sản phẩm của bạn, và không thể hoàn tác'
                            onclose={() => setDeleteProductId(null)}
                            onSucess={() => {
                                handleDeleteProduct(deleteProductId)
                                setDeleteProductId(null)
                            }}
                        />

                        </React.Fragment>
                    ))}
                    </tbody>
                </table>

                {products.length > 0 && <Pagination
                    page={page}
                    setPage={setPage}
                    limit={limit}
                    setLimit={setLimit}
                    maxPage={totalPages}
                />}
            </div>

            {isLoading && <TableLoading/>}

            {products.length === 0 && (
                <div className="w-full bg-white shadow-md rounded-sm h-80 mt-3 flex items-center justify-center">
                    <div className="text-center">
                    <LuPackageX className="text-blue-300 text-8xl mx-auto mb-2"/>
                    <p className="text-xl">Không có sản phẩm nào</p>
                    </div>
                </div>
            )}
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}