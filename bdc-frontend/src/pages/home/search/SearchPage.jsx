import FilterSidebar from './FilterSidebar'
import SearchFilter from './SearchFilter'
import ProductGrid from './ProductGrid'
import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { BASE_API_URL } from '../../../constants'
import { PiListMagnifyingGlass } from "react-icons/pi";
import { GoLightBulb } from "react-icons/go";
import Pagination from './Pagination'

export default function SearchPage(){

  const [searchParams, setSearchParams] = useSearchParams()
  const [isLoading, setIsLoading] = useState(false)
  const [isEmpty, setIsEmpty] = useState(false)
  const limit = 60
  const [page, setPage] = useState(searchParams.get('page') || 0)
  const [totalPages, setTotalPages] = useState(0)
  const [keyword] = useState(searchParams.get('keyword'))
  const [sort, setSort] = useState({
    sortBy: searchParams.get('sortBy') || "relevance",
    order: searchParams.get('order') || "desc"
  })
  const [filters, setFilters] = useState({
    categoryIds: searchParams.getAll('categoryIds') || [],
    locations: searchParams.getAll('locations') || [],
    minPrice: searchParams.get('minPrice') || null,
    maxPrice: searchParams.get('maxPrice') || null,
    minRating: searchParams.get('minRating') || null
  })

  const resetFilters = () => {
    setFilters({
      categoryIds: [],
      locations: [],
      minPrice: null,
      maxPrice: null,
      minRating: null
    })
  }
  const [products, setProducts] = useState([])

  const searchProducts = () => {
    setIsLoading(true)
    setIsEmpty(false)
    fetch(`${BASE_API_URL}/v1/homepage/search?limit=${limit}&${searchParams.toString()}`)
      .then(res => res.json())
      .then(res => {
        setIsEmpty(res.numberOfElements === 0)
        setTotalPages(res.totalPages)
        setProducts(res.content)
      })
      .finally(() => setIsLoading(false))
  }

  useEffect(() => {
    const params = {
      page,
      keyword,
      sortBy: sort.sortBy,
      order: sort.order,
      categoryIds: filters.categoryIds,
      locations: filters.locations,
      minPrice: filters.minPrice,
      maxPrice: filters.maxPrice,
      minRating: filters.minRating
    }
    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== "")
    );
    setSearchParams(filteredParams)
  }, [sort, filters, page])

  useEffect(() => {
    searchProducts()
  }, [searchParams])

  return (
      <div className="flex gap-3 p-10 pt-5">
            <div className="w-full md:w-[240px] sticky top-4 self-start">
              <FilterSidebar 
                filters={filters} 
                setFilters={setFilters}
                resetFilters={resetFilters}/>
            </div>
            <div className="flex-1">
              <div className="flex items-center text-l ml-1 mb-2">
                <span className="text-gray-500 flex gap-2 items-center">
                  <GoLightBulb size={16}/> Kết quả tìm kiếm cho từ khóa '<span className="text-blue-500 font-medium">{keyword}</span>'
                </span>
              </div>
              <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 mb-4 w-full">
                <SearchFilter sort={sort} setSort={setSort} page={page} setPage={setPage} totalPages={totalPages}/>
              </div>
              {isLoading && (
                <div role="status" className="flex justify-center mt-2">
                  <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/>
                      <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/>
                  </svg>
                </div>
              )}
              {!isEmpty ? 
                (
                  <>
                    <ProductGrid products={products}/>
                    <Pagination page={page} setPage={setPage} totalPages={totalPages}/>
                  </>
                )
                : (
                  <div className="w-fullh-100 mt-3 flex items-center justify-center">
                      <div className="text-center">
                      <PiListMagnifyingGlass className="text-blue-500 text-8xl mx-auto mb-2"/>
                      <p className="text-xl">Không tìm thấy sản phẩm với từ khóa '<span className="text-blue-500 font-medium">{keyword}</span>'</p>
                      </div>
                  </div>
                )} 
            </div>
      </div>
  )
}