import FilterSidebar from './FilterSidebar'
import SearchFilter from './SearchFilter'
import ProductGrid from './ProductGrid'

export default function SearchPage(){

    return (
        <div className="flex gap-3 p-10 pt-5">
            <div className="flex flex-col md:flex-row gap-4 items-start">
            <div className="w-full md:w-[240px] sticky top-4 self-start">
              <FilterSidebar />
            </div>
            <div className="flex-1">
              <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 mb-4">
                <SearchFilter />
              </div>
              <ProductGrid />
            </div>
          </div>
        </div>
    )
}