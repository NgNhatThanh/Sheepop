export default function ProductDescriptionSection({ product }){
    return (
        <div className="w-300 bg-white rounded-sm mx-auto p-6 mb-4">    
            <div>
                <h2 className="bg-gray-100 p-3 font-semibold text-xl">
                    MÔ TẢ SẢN PHẨM
                </h2>
                <p className="p-3">
                    {product.description}
                </p>
            </div>
        </div>
    )
}