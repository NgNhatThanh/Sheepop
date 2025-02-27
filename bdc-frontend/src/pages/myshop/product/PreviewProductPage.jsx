import ErrorPage, { ERROR_TYPE } from "./../../product/ErrorPage"
import { useParams } from "react-router-dom"
import { useState, useEffect } from "react"
import { BASE_API_URL } from "../../../constants/index"
import ProductSection from "./../../product/ProductSection"
import ShopSection from "./../../product/ShopSection"
import ProductDescriptionSection from "./../../product/ProductDescriptionSection"
import ReviewSection from "./../../product/ReviewSection"
import { fetchWithAuth } from "../../../util/AuthUtil"

export default function PreviewProductPage(){
    const { productId } = useParams();
    const [isLoading, setIsLoading] = useState(true)
    const [product, setProduct] = useState(null)
    const [error, setError] = useState(null)
    
    useEffect(() => {
        async function fetchProduct() {
            try{
                const productResponse = await fetchWithAuth(`${BASE_API_URL}/v1/shop/product/preview/${productId}`)
                if(productResponse.status === 200){
                    const data = await productResponse.json()
                    setProduct(data)
                    setIsLoading(false)
                }
                else if (productResponse.status === 404) {
                    setError(ERROR_TYPE.PRODUCT_NOT_EXIST)
                } 
                else{   
                    setError(ERROR_TYPE.UNKNOWN_ERROR)
                }
                
            }
            catch(err){
                setError(ERROR_TYPE.UNKNOWN_ERROR)
            }
        }

        fetchProduct()
    }, [productId])

    if(error){
        return <ErrorPage errorType={error}/>
    }

    if(isLoading){
        return (
            <div> 
                <p>Loading...</p>
            </div>
        )
    }
    
    return (
        <>
            <ProductSection product={product} isPreview={true}/>
            <ShopSection shop={product.shop}/>
            <ProductDescriptionSection product={product}/>
            <ReviewSection product={product} />
        </>
    )

}