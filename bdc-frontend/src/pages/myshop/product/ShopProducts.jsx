import { useState } from "react"
import AddProduct from "./AddProduct"
import ProductTable from './ProductTable'

export default function ShopProducts(){
    
    const [isAddingProduct, setIsAddingProduct] = useState(false)

    const handleAddProduct = () => {
        setIsAddingProduct(true)
    }

    const handleBackToList = () => {
        setIsAddingProduct(false)
    }
    
    return (
        <>
            {!isAddingProduct ? (
                <div>
                    <button onClick={handleAddProduct}>
                        Add product
                    </button>
                    <ProductTable/>
                </div>
            ) : (
                <AddProduct onBack={handleBackToList}/>
            )}
        </>
    )
}