import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { fetchWithAuth } from '../../../util/AuthUtil'
import { BASE_API_URL } from "../../../constants";
import Pagination from '../../../pages/common/Pagination'
import { toast } from "react-toastify";
import { ImNewTab } from "react-icons/im";
import RestrictProductForm from "./RestrictProductForm";
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import TableLoading from "../../common/TableLoading";
import { LuPackageX } from "react-icons/lu";

const tabs = [
    "Đang kích hoạt",
    "Bị đình chỉ",
    "Đã điều chỉnh"
];

export default function ProductList(){

    const navigate = useNavigate()
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const currentType = parseInt(searchParams.get("type")) || 0;

    const [isLoading, setIsLoading] = useState(false)
    const [productName, setProductName] = useState("")
    const [shopName, setShopName] = useState("")
    const [productNameSearchKey, setProductNameSearchKey] = useState("")
    const [shopNameSearchKey, setShopNameSearchKey] = useState("")
    const [page, setPage] = useState(1)
    const [limit, setLimit] = useState(10)
    const [maxPage, setMaxPage] = useState(1)

    const [totalProducts, setTotalProducts] = useState(0)
    const [products, setProducts] = useState([])

    const [detailProduct, setDetailProduct] = useState(null)
    const [restrictProductId, setRestrictProductId] = useState(false)

    const [unrestrictProductId, setUnrestrictProductId] = useState(null)

    const handleUnrestrictProduct = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/admin/product/open_restrict/${productId}`, "/", true, {
            method: "POST"
        })
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
                }
                else{
                    toast.success("Gỡ đình chỉ thành công")
                    setProducts(prev => prev.filter(prod => prod.id !== productId))
                }
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau"))
    }

    const fetchDetailProduct = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/admin/product/detail/${productId}`)
            .then(res => res.json())
            .then(res => setDetailProduct(res))
            .catch(err => {
                console.log("Err: ", err)
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
            })
    }

    const fetchProducts = () => {
        setIsLoading(true)
        fetchWithAuth(`${BASE_API_URL}/v1/admin/product/get_list?type=${currentType}&productName=${productName}&shopName=${shopName}&page=${page-1}&limit=${limit}`)
            .then(res => res.json())
            .then(res => {
                setMaxPage(res.totalPages)
                setTotalProducts(res.totalElements)
                setProducts(res.content)
            })
            .catch(err => {
                console.log("Err: ",err)
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
            })
            .finally(() => setIsLoading(false))
    }

    useEffect(() => {
        fetchProducts()
    }, [currentType, page, limit, productName, shopName])

    const displayCategory = (cat) => {
        let disp = cat.name
        cat = cat.parent
        while(cat){
            disp += ' > ' + cat.name 
            cat = cat.parent
        }
        return disp
    }

    const successRestrictProduct = () => {
        toast.success("Đình chỉ sản phẩm thành công")
        setProducts(prev => prev.filter(prod => prod.id !== restrictProductId))
        setRestrictProductId(null)
    }

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
                    onClick={() => navigate(`/admin/product?type=${index}`)}
                >
                    {tab}
                </button>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-3 bg-white p-4 rounded mt-4 shadow-md">
                <input
                    type="text"
                    value={productNameSearchKey}
                    placeholder="Tìm theo tên sản phẩm"
                    onChange={e => setProductNameSearchKey(e.target.value)}
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <input
                    type="text"
                    value={shopNameSearchKey}
                    placeholder="Tìm theo tên cửa hàng"
                    onChange={e => setShopNameSearchKey(e.target.value)}
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        setProductName(productNameSearchKey)
                        setShopName(shopNameSearchKey)
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        setProductName('')
                        setShopName('')
                        setProductNameSearchKey('')
                        setShopNameSearchKey('')
                    }}
                >
                    Đặt lại
                </button>
            </div>

            <p className="text-xl font-semibold mb-2 mt-4">{totalProducts} sản phẩm</p>
            <div className="container mx-auto bg-white rounded-sm ">
                <table className="table-auto w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className="bg-white w-full">
                            <th className="border border-gray-300 p-2 w-fit">STT</th>
                            <th className="border border-gray-300 p-2 w-2/3">Tên</th>
                            <th className="border border-gray-300 p-2">Giá</th>
                            <th className="border border-gray-300 p-2">Thời gian tạo</th>
                            <th className="border border-gray-300 p-2">Thao tác</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                        {products.map((product, index) => (
                            <tr key={index} id={product.id}>
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
                                    <div className="flex gap-1 w-fit cursor-pointer items-center">
                                        <p className="text-sm text-gray-600 hover:text-gray-800">Cửa hàng: {product.shopName}</p>
                                        <ImNewTab/>
                                    </div>
                                </td>
                                <td className="border border-gray-300 p-2 text-center">{product.price === -1 ? '---' : product.price.toLocaleString() + ' VND'}</td>
                                <td className="border border-gray-300 p-2 text-center">{new Date(product.createdAt).toLocaleDateString()}</td>
                                <td className="border border-gray-300 p-2">
                                    <div className="flex flex-col gap-1">
                                        <button 
                                            className="text-white text-center cursor-pointer rounded-xs bg-blue-400 p-1 hover:bg-blue-500"
                                            onClick={() => fetchDetailProduct(product.id)}
                                        >
                                            Chi tiết
                                        </button>

                                        {(!product.restricted || product.restrictStatus !== 'RESTRICTED') && (
                                            <button 
                                                className="text-white text-center cursor-pointer rounded-xs bg-red-400 p-1 hover:bg-red-500"
                                                onClick={() => setRestrictProductId(product.id)}
                                            >
                                                Đình chỉ
                                            </button>
                                        )} 

                                        {product.restricted && product.restrictStatus === 'PENDING' && (
                                            <button 
                                                className="text-white text-center cursor-pointer rounded-xs bg-red-400 p-1 hover:bg-red-500"
                                                onClick={() => setUnrestrictProductId(product.id)}
                                            >
                                                Gỡ đình chỉ
                                            </button>
                                        )}
                                    </div>
                                </td>
                                <Dialog
                                    open={unrestrictProductId !== null}
                                    onClose={() => setUnrestrictProductId(null)}
                                    aria-labelledby="alert-dialog-title"
                                    aria-describedby="alert-dialog-description"
                                >
                                <DialogTitle id="alert-dialog-title">
                                    {"Gỡ đình chỉ sản phẩm?"}
                                </DialogTitle>
                                <DialogContent>
                                    <DialogContentText id="alert-dialog-description">
                                        Sản phẩm này sẽ được phép đăng bán trở lại
                                    </DialogContentText>
                                </DialogContent>
                                <DialogActions>
                                    <Button onClick={() => setUnrestrictProductId(null)}>Hủy</Button>
                                    <Button onClick={() => {
                                        handleUnrestrictProduct(unrestrictProductId)
                                        setUnrestrictProductId(null)
                                    }} autoFocus>
                                        Đồng ý
                                    </Button>
                                </DialogActions>
                                </Dialog>
                            </tr>
                            ))
                        }
                    </tbody>
                </table>

                {products.length > 0 && <Pagination
                    page={page}
                    setPage={setPage}
                    limit={limit}
                    setLimit={setLimit}
                    maxPage={maxPage}
                />}
            </div>

            {restrictProductId && 
                <RestrictProductForm
                    closeForm={() => setRestrictProductId(null)}
                    productId={restrictProductId}
                    onSuccess={successRestrictProduct}
                />}

            {detailProduct && (
                <div className="fixed inset-0 z-10 flex justify-center items-center bg-gray-50/90">
                    <div className="bg-white w-200 min-h-2/3 p-4">   
                        <div className="flex justify-between rounded-sm items-center mb-4">
                            <h2 className="text-xl font-bold">{detailProduct.name}</h2>
                            <button onClick={() => setDetailProduct(null)} className="cursor-pointer text-blue-500">✖</button>
                        </div>

                        <div className="max-h-105 overflow-auto">

                            {detailProduct.restricted && (
                                <div className="mb-5">
                                    <p className="font-bold text-red-500">Lý do hạn chế</p>
                                    <p>{detailProduct.restrictedReason}</p>
                                </div>
                            )}
                            
                            <div>
                                <p className="font-bold">Mô tả</p>
                                <p>{detailProduct.description}</p>
                            </div>

                            <div>
                                <p className="font-bold mt-2">Đăng vào</p>
                                <p>{new Date(detailProduct.createdAt).toLocaleDateString()}</p>
                            </div>

                                <div>
                                <p className="font-bold mt-2">Cập nhật vào</p>
                                <p>{new Date(detailProduct.updatedAt).toLocaleDateString()}</p>
                            </div>

                            {detailProduct.skuList.length === 0 && (
                                <div>
                                    <p className="font-bold mt-2">Giá</p>
                                    <p>{detailProduct.price.toLocaleString()} VND</p>
                                </div>
                            )}

                            <div className="mt-2">
                                <p className="font-bold">Hình đại diện</p>
                                <img src={detailProduct.thumbnailUrl} className="w-30 rounded-sm mt-2"/>
                            </div>

                            <div className="mt-2">
                                <p className="font-bold">Hình sản phẩm</p>
                                <div className="flex gap-2 mt-2">
                                    {detailProduct.mediaList.map(media => (
                                        <img src={media.url} className="w-25 rounded-sm"/>
                                    ))}
                                </div>
                            </div>

                            <div className="mt-2">
                                <p className="font-bold">Ngành hàng</p>
                                <p>{displayCategory(detailProduct.category)}</p>
                            </div>

                            {detailProduct.skuList.length > 0 && (
                                <div className="mt-2">
                                    <p className="font-bold">Phân loại</p>
                                    <table className="w-full mt-2">
                                        <thead>
                                            <tr className="bg-gray-100">
                                                <td className="border-1 text-center font-bold">Thông tin</td>
                                                <td className="border-1 text-center font-bold">Giá</td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {detailProduct.skuList.map(sku => (
                                                <tr className="border-collapse">
                                                    <td className="border-1 text-center">
                                                        {sku.attributes.map(attr => (
                                                            <p>{attr.name}: {attr.value}</p>
                                                        ))}
                                                        <p>{sku.sku}</p>
                                                    </td>
                                                    <td className="border-1 text-center">
                                                        {sku.price.toLocaleString()} VND
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}

                            <div className="mt-2">
                                <p className="font-bold">Cân nặng</p>
                                <p>{detailProduct.weight} g</p>
                            </div>
                        </div>
                    </div>                    
                </div>
            )}

            {isLoading && <TableLoading/>}

            {products.length === 0 && (
                <div className="w-full bg-white shadow-md rounded-sm h-80 mt-3 flex items-center justify-center">
                    <div className="text-center">
                    <LuPackageX className="text-blue-300 text-8xl mx-auto mb-2"/>
                    <p className="text-xl">Không có sản phẩm nào</p>
                    </div>
                </div>
            )}
        </div>
    )
}