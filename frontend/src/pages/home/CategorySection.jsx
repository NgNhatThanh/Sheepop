import { useEffect, useState } from "react"
import { BASE_API_URL } from "../../constants"
import { Link } from 'react-router-dom'

export default function CategorySection(){

    const [categories, setCategories] = useState([])

    useEffect(() => {
        const fetchCategories = () => {
            fetch(`${BASE_API_URL}/v1/homepage/get_display_categories`)
                .then(res => res.json())
                .then(res => setCategories(res))
        }

        fetchCategories()
    }, [])

    return (
        <div className="bg-white w-full max-w-6xl mx-auto overflow-hidden rounded-xs shadow-lg p-4">
            <h2 className="text-2xl font-semibold">
                Khám phá danh mục
            </h2>

            <div className="mt-3 w-full flex gap-10 justify-center">
                {categories.map(cat => (
                    <div 
                        key={cat.id}
                        className="max-w-20 rounded-md hover:bg-blue-50"
                    >
                        <Link 
                            to={`search?categoryIds=${cat.category.id}`}
                            className="flex flex-col justify-center items-center"    
                        >
                            <img
                                src={cat.thumbnailUrl}
                                className="w-17 rounded-sm"
                            />
                            <p className="text-center">{cat.category.name}</p>
                        </Link>
                    </div>
                ))}
            </div>
        </div>
    )
}