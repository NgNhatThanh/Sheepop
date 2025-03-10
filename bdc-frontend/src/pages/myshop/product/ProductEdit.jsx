import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import Loading from "../../common/Loading"
import SaveProduct from "./SaveProduct"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"

export default function ProductEdit(){

    const { productId } = useParams()
    const [ product, setProduct ] = useState(null)

    useEffect(() => {
        const fetchProduct = (productId) => {
            fetchWithAuth(`${BASE_API_URL}/v1/shop/product/${productId}`, '/myshop/product-list', true)
                .then(res => {
                    if(res.ok) return res.json()
                    window.location.assign('/myshop/product-list')
                })
                .then(res => setProduct(res))
                .catch(() => {
                    alert("Có lỗi xảy ra, vui lòng thử lại sau")
                    window.location.assign("/myshop/product-list")
                })
        }

        fetchProduct(productId)
    }, [productId])

    return (
        <>
        {product === null ? (
            <div className="flex justify-center">
              <Loading/>  
            </div>
            
        ): (
            <SaveProduct curProduct={product}/>
        )}
        </>
    )

}