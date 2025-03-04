import React, { useEffect, useState } from "react";
import Pagination from "../../common/Pagination";
import { fetchWithAuth } from "../../../util/AuthUtil";
import { BASE_API_URL } from "../../../constants";
import { ToastContainer, toast } from "react-toastify";


const levelColors = [
    "#ffffff", 
    "#f3f4f6", 
    "#e8f0fe", 
    "#fef3c7", 
    "#fce7f3", 
];

export default function CategoryPage() {
    const [categories, setCategories] = useState([]);
    const [expandedCategories, setExpandedCategories] = useState({});
    const [page, setPage] = useState(1);
    const [limit, setLimit] = useState(10);
    const [maxPage, setMaxPage] = useState(1);
    const [sortType, setSortType] = useState(1);
    const [keyword, setKeyword] = useState("");
    const [searchText, setSearchText] = useState("");
    const [editingCategory, setEditingCategory] = useState(null); 
    const [editData, setEditData] = useState({ name: "", description: "" });

    const [newCategory, setNewCategory] = useState(null)

    useEffect(() => {
        const delay = setTimeout(() => {
            setKeyword(searchText); 
        }, 500);

        return () => clearTimeout(delay); 
    }, [searchText]);

  const fetchCategories = () => {
    fetchWithAuth(
      `${BASE_API_URL}/v1/admin/category/get_list?page=${page - 1}&limit=${limit}&sortType=${sortType}&keyword=${keyword}`
    )
      .then((res) => res.json())
      .then((res) => {
        setCategories(res.content);
        setMaxPage(res.totalPages);
      })
      .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau"));
  };

  const fetchSubCategories = (parentId) => {
    fetchWithAuth(
      `${BASE_API_URL}/v1/admin/category/get_sub_categories?parentId=${parentId}`
    )
      .then((res) => res.json())
      .then((res) => {
        setExpandedCategories((prev) => ({
          ...prev,
          [parentId]: res,
        }));
      })
      .catch(() => toast.error("Không thể tải danh mục con"));
  };

  const toggleCategory = (categoryId) => {
    setExpandedCategories((prev) => {
      const isExpanded = !!prev[categoryId];
      if (isExpanded) {
        const newExpanded = { ...prev };
        delete newExpanded[categoryId];
        return newExpanded;
      } else {
        fetchSubCategories(categoryId);
        return { ...prev, [categoryId]: [] };
      }
    });
  };

    const handleEdit = (cat) => {
        setEditingCategory(cat.id);
        setEditData({ name: cat.name, description: cat.description });
    };

    const handleCancelEdit = () => {
        setEditingCategory(null);
        setEditData({ name: "", description: "" });
    };

    const handleChange = (e) => {
        setEditData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleChangeNewCategory = (e) => {
      setNewCategory(prev => ({ ...prev, [e.target.name]: e.target.value}))
    }

    const handleAddNewCategory = () => {
      if(!newCategory.name || !newCategory.description){
        toast.warn("Dữ liệu không được để trống")
        return
      }

      fetchWithAuth(`${BASE_API_URL}/v1/admin/category/add`, "/", true, {
        method: "POST",
        headers :{
          'content-type': 'application/json'
        },
        body: JSON.stringify(newCategory)
      })
        .then(res => res.json())
        .then(res => {
          if(res.message){
            toast.error(res.message)
            return
          }
          if(res.parentId){
            setExpandedCategories(prev => ({
              ...prev,
              [res.parentId]: [res, ...(prev[res.parentId] || [])]
            }))
          }
          else{
            setCategories(prev => [res, ...prev])
          }
          setNewCategory(null)
          toast.success("Thêm danh mục thành công")
        })
        .catch(err => {
          console.log(err)
          toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
        })
    }

    const handleSaveEdit = () => {
        if(!editData.name || !editData.description){
            toast.warn("Dữ liệu không được trống")
            return
        }
        fetchWithAuth(`${BASE_API_URL}/v1/admin/category/update/${editingCategory}`, "/", true, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(editData),
        })
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error(res.message)
                }
                else{
                    toast.success("Cập nhật thành công!");
                    const parentId = res.parentId
                    const catId = res.id
                    if(parentId){
                        setExpandedCategories(prev => ({
                            ...prev,
                            [parentId]: expandedCategories[parentId].map(child => 
                                child.id === catId ? { ...child, ...editData } : child
                            )
                        }))
                    }
                    else{
                        setCategories(prev => prev.map(cat => 
                            cat.id === catId ? { ...cat, ...editData} : cat
                        ))
                    }
                    setEditingCategory(null);
                }
            })
            .catch((err) => {
                console.log(err)
                toast.error("Cập nhật thất bại, vui lòng thử lại sau")
            });
    };

  useEffect(() => {
    fetchCategories();
  }, [page, limit, sortType, keyword]);

  const renderPrefix = (level) => {
    const prefix = [];
    for (let i = 0; i < level; i++) {
      if (i === level - 1) {
        prefix.push(
          <span key={i} className="text-gray-500">
            |--&nbsp;
          </span>
        );
      } else {
        prefix.push(
          <span key={i} className="text-gray-500">
            |&nbsp;&nbsp;&nbsp;
          </span>
        );
      }
    }
    return prefix;
  };

  
  const renderCategories = (cats, level = 0) => {
    return cats.map((cat) => (
      <React.Fragment key={cat.id}>
        <tr style={{ backgroundColor: levelColors[level % levelColors.length] }}>
            <td className="border border-gray-300 p-2">
                <div className="flex items-center">
                    {editingCategory === cat.id ? (
                        <input
                            type="text"
                            name="name"
                            className="bg-white border border-gray-300 rounded-lg p-2 w-full"
                            value={editData.name}
                            onChange={handleChange}
                        />
                    ) : (
                        <>
                        {renderPrefix(level)}
                        {cat.hasChildren && (
                            <span
                            className="cursor-pointer"
                            onClick={() => toggleCategory(cat.id)}
                            >
                            {expandedCategories[cat.id] ? "⏷" : "⏵"}
                            </span>
                        )}
                        <p className={cat.hasChildren ? "ml-1" : "ml-4"}>{cat.name}</p> 
                        {(cat.hasChildren || cat.productCount === 0) && (
                          <button 
                            className="ml-2 text-sm text-white bg-blue-400 cursor-pointer rounded-sm p-1 hover:bg-blue-500"
                            onClick={() => setNewCategory({
                              parentId: cat.id,
                              name: "",
                              description: ""
                            })}
                          >
                            Thêm
                          </button>
                        )}
                        </>
                    )}
                </div>
            </td>
            <td className="border border-gray-300 p-2">
                {editingCategory === cat.id ? (
                    <textarea
                        name="description"
                        className="bg-white border border-gray-300 rounded-lg p-2 w-full"
                        value={editData.description}
                        onChange={handleChange}
                    />
                ) : (
                    <p>{cat.description}</p>
                )}
            </td>
            <td className="border border-gray-300 p-2 text-center">
                {cat.productCount}
            </td>
            <td className="border border-gray-300 p-2 text-center">
                {new Date(cat.createdAt).toLocaleDateString()}
            </td>
            <td className="border border-gray-300 p-2 text-center">
                {editingCategory === cat.id ? (
                    <div className="flex flex-col justify-center gap-1">
                        <button
                            onClick={handleSaveEdit}
                            className="cursor-pointer bg-blue-500 text-white px-3 py-1 rounded-lg hover:bg-blue-600"
                        >
                            Lưu
                        </button>
                        <button
                            onClick={handleCancelEdit}
                            className="cursor-pointer bg-gray-400 text-white px-3 py-1 rounded-lg hover:bg-gray-500"
                        >
                            Hủy
                        </button>
                    </div>
                ) : (
                    <button
                        onClick={() => handleEdit(cat)}
                        className="bg-blue-500 cursor-pointer text-white px-3 py-1 rounded-lg hover:bg-blue-600"
                    >
                        Sửa
                    </button>
                )}
            </td>
        </tr>
        {newCategory && newCategory.parentId === cat.id && renderNewCategoryField()}
        {expandedCategories[cat.id] &&
          renderCategories(expandedCategories[cat.id], level + 1)}
      </React.Fragment>
    ));
  };

  const renderNewCategoryField = () => {
    return (
      <tr >
          <td className="border border-gray-300 p-2">
            <input
                type="text"
                name="name"
                className="bg-white border border-gray-300 rounded-lg p-2 w-full"
                value={newCategory.name}
                onChange={handleChangeNewCategory}
            />
          </td>
          <td className="border border-gray-300 p-2">
            <textarea
                name="description"
                className="bg-white border border-gray-300 rounded-lg p-2 w-full"
                value={newCategory.description}
                onChange={handleChangeNewCategory}
            />
          </td>
          <td className="border border-gray-300 p-2 text-center">
          </td>
          <td className="border border-gray-300 p-2 text-center">
          </td>
          <td className="border border-gray-300 p-2 text-center">
            <div className="flex flex-col gap-1">
              <button
                  onClick={handleAddNewCategory}
                  className="cursor-pointer bg-blue-500 text-white px-3 py-1 rounded-lg hover:bg-blue-600"
              >
                  Thêm
              </button>
              <button
                  onClick={() => setNewCategory(null)}
                  className="cursor-pointer bg-gray-400 text-white px-3 py-1 rounded-lg hover:bg-gray-500"
              >
                  Hủy
              </button>
            </div>
          </td>
      </tr>
    )
  }
  

  return (
    <div>
      <div className="bg-white p-4 shadow-xs rounded-sm">
        <div className="flex justify-between items-center gap-4">
            <input
                type="text"
                placeholder="Tìm kiếm danh mục..."
                className="border border-gray-300 rounded-lg p-2 w-1/3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
            />
          <div className="flex gap-2 items-center">
            <select 
                className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                onChange={(e) => setSortType(Number(e.target.value))}    
            >
              <option value="1">Số lượng sản phẩm (giảm dần)</option>
              <option value="2">Số lượng sản phẩm (tăng dần)</option>
              <option value="3">Ngày thêm vào (mới nhất)</option>
              <option value="4">Ngày thêm vào (cũ nhất)</option>
            </select>
            <button 
              className="cursor-pointer bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
              onClick={() => setNewCategory({
                parentId: null,
                name: "",
                description: ""
              })}  
            >
              + Thêm danh mục lớn
            </button>
          </div>
        </div>
      </div>

      <p className="text-xl font-semibold mt-4"> {categories.length} danh mục </p>

      <div className="mt-2 min-h-screen rounded-sm bg-white">
        <table className="w-full border-collapse border border-gray-300">
          <thead>
            <tr className="bg-gray-100">
              <th className="border border-gray-300 p-2 w-1/2">Tên</th>
              <th className="border border-gray-300 p-2 w-1/4">Mô tả</th>
              <th className="border border-gray-300 p-2">Số lượng sản phẩm</th>
              <th className="border border-gray-300 p-2">Thêm vào ngày</th>
              <th className="border border-gray-300 p-2">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {newCategory && newCategory.parentId === null && renderNewCategoryField()}
            {renderCategories(categories)}
          </tbody>
        </table>

        <Pagination
          page={page}
          limit={limit}
          setPage={setPage}
          setLimit={setLimit}
          maxPage={maxPage}
        />
      </div>

      
      <ToastContainer position="bottom-right" />
    </div>
  );
}
