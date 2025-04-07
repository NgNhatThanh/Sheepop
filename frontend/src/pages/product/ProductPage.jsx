import ErrorPage, { ERROR_TYPE } from "./ErrorPage"
import { useState, useEffect } from "react"
import { BASE_API_URL } from "../../constants"
import ProductSection from "./ProductSection"
import ShopSection from "./ShopSection"
import ProductDescriptionSection from "./ProductDescriptionSection"
import ReviewSection from "./ReviewSection"

export default function ProductPage(){
    const parts = window.location.pathname.split('.')
    if(parts.length < 2){
        return <ErrorPage errorType={ERROR_TYPE.INFOMATION_MISSING}/>
    }
    const [isLoading, setIsLoading] = useState(true)
    const [product, setProduct] = useState(null)
    const [error, setError] = useState(null)
    
    const productId = parts.at(-1)
    useEffect(() => {
        async function fetchProduct() {
            try{
                const productResponse = await fetch(`${BASE_API_URL}/v1/product/${productId}`)
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
            <ProductSection product={product}/>
            <ShopSection shop={product.shop}/>
            <ProductDescriptionSection product={product}/>
            <ReviewSection product={product} />
        </>
    )

}