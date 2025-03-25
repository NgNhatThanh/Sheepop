"use client"

import { useEffect, useState } from "react"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"
import { toast } from "react-toastify"
import { uploadImage } from "../../../util/UploadUtil"
import { FiPlus, FiImage, FiCheck, FiTrash2 } from "react-icons/fi"

export default function DisplayCatManage() {
  const [parentCats, setParentCats] = useState([])
  const [displayCats, setDisplayCats] = useState([])

  const handleAddDisplayCat = (catId) => {
    fetchWithAuth(`${BASE_API_URL}/v1/admin/content/add_display_cat?categoryId=${catId}`, window.location, true, {
      method: "POST",
    })
      .then((res) => res.json())
      .then((res) => {
        if (res.message) {
          toast.error("Có lỗi khi thêm danh mục hiển thị")
        } else {
          setDisplayCats((prev) => [res, ...prev])
        }
      })
  }

  const handleDeleteDisplayCat = (catId) => {
    fetchWithAuth(`${BASE_API_URL}/v1/admin/content/delete_display_cat?categoryId=${catId}`, window.location, true, {
      method: "POST",
    })
      .then((res) => res.json())
      .then((res) => {
        if (res.message) {
          toast.error("Có lỗi khi xóa danh mục hiển thị")
        } else {
          setDisplayCats((prev) => prev.filter((cat) => cat.category.id !== catId))
        }
      })
  }

  const handleChangeThumbnail = async (disCatId, image) => {
    const imgUrl = await uploadImage(image)
    console.log(imgUrl)
    fetchWithAuth(`${BASE_API_URL}/v1/admin/content/update_display_cat?disCatId=${disCatId}`, window.location, true, {
      method: "POST",
      headers: {
        "content-type": "application/json",
      },
      body: JSON.stringify({
        thumbnailUrl: imgUrl,
      }),
    })
      .then((res) => res.json())
      .then((res) => {
        if (res.message) {
          toast.error("Có lỗi khi cập nhật danh mục hiển thị")
        } else {
          setDisplayCats((prev) =>
            prev.map((cat) => {
              if (cat.id === res.id) cat = res
              return cat
            }),
          )
          toast.success("Cập nhật thành công")
        }
      })
  }

  useEffect(() => {
    const fetchParentCats = () => {
      fetchWithAuth(`${BASE_API_URL}/v1/admin/content/get_parent_cats`)
        .then((res) => res.json())
        .then((res) => {
          if (res.message) {
            toast.error("Có lỗi khi tải danh mục cha")
          } else {
            setParentCats(res)
          }
        })
    }

    const fetchDisplayCats = () => {
      fetchWithAuth(`${BASE_API_URL}/v1/admin/content/get_display_categories`)
        .then((res) => res.json())
        .then((res) => {
          if (res.message) {
            toast.error("Có lỗi khi tải danh mục hiển thị")
          } else {
            setDisplayCats(res)
          }
        })
    }

    fetchParentCats()
    fetchDisplayCats()
  }, [])

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-3">Danh mục hiển thị</h2>

      <div className="flex flex-col md:flex-row gap-6">
        {/* Left panel - Category list */}
        <div className="w-full md:w-1/3 bg-gray-50 rounded-lg p-4 shadow-sm">
          <h3 className="font-bold text-lg text-gray-700 mb-4 border-b pb-2">Danh sách danh mục</h3>
          <div className="space-y-3 max-h-[500px] overflow-y-auto pr-2">
            {parentCats.map((cat) => (
              <div
                key={cat.id}
                className="flex items-center space-x-2 hover:bg-gray-100 p-2 rounded-md transition-colors"
              >
                <div className="relative flex items-center">
                  <input
                    type="checkbox"
                    id={cat.id}
                    checked={displayCats.some((dc) => dc.category.id === cat.id)}
                    onChange={(e) => {
                      const checked = e.target.checked
                      if (!checked) {
                        handleDeleteDisplayCat(cat.id)
                      } else {
                        handleAddDisplayCat(cat.id)
                      }
                    }}
                    className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2"
                  />
                  {displayCats.some((dc) => dc.category.id === cat.id) && (
                    <FiCheck className="absolute text-green-500 text-xs right-0 bottom-0" />
                  )}
                </div>
                <label htmlFor={cat.id} className="text-gray-700 font-medium cursor-pointer flex-1 truncate">
                  {cat.name}
                </label>
                {displayCats.some((dc) => dc.category.id === cat.id) ? (
                  <button
                    onClick={() => handleDeleteDisplayCat(cat.id)}
                    className="text-red-500 hover:text-red-700 p-1 rounded-full hover:bg-red-50"
                  >
                    <FiTrash2 size={16} />
                  </button>
                ) : (
                  <button
                    onClick={() => handleAddDisplayCat(cat.id)}
                    className="text-blue-500 hover:text-blue-700 p-1 rounded-full hover:bg-blue-50"
                  >
                    <FiPlus size={16} />
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Right panel - Details */}
        <div className="w-full md:w-2/3 bg-gray-50 rounded-lg p-4 shadow-sm">
          <h3 className="font-bold text-lg text-gray-700 mb-4 border-b pb-2">Chi tiết danh mục hiển thị</h3>

          {displayCats.length === 0 ? (
            <div className="text-center py-10 text-gray-500">
              <FiImage className="mx-auto mb-2" size={40} />
              <p>Chưa có danh mục hiển thị nào được chọn</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {displayCats.map((cat, index) => (
                <div
                  key={cat.id}
                  className="flex items-center gap-3 bg-white p-3 rounded-md shadow-sm hover:shadow-md transition-shadow"
                >
                  <div className="flex items-center justify-center bg-gray-200 w-8 h-8 rounded-full text-gray-700 font-semibold">
                    {index + 1}
                  </div>
                  <div className="relative group">
                    <input
                      type="file"
                      id={`thumbnail-${cat.id}`}
                      onChange={(e) => handleChangeThumbnail(cat.id, e.target.files[0])}
                      hidden
                    />
                    <label htmlFor={`thumbnail-${cat.id}`} className="block cursor-pointer">
                      {cat.thumbnailUrl ? (
                        <div className="relative">
                          <img
                            src={cat.thumbnailUrl || "/placeholder.svg"}
                            alt={cat.category.name}
                            className="w-16 h-16 object-cover rounded-md border-2 border-gray-200"
                          />
                          <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity rounded-md">
                            <FiImage className="text-white" size={20} />
                          </div>
                        </div>
                      ) : (
                        <div className="w-16 h-16 bg-gray-200 rounded-md flex items-center justify-center">
                          <FiImage className="text-gray-500" size={20} />
                        </div>
                      )}
                    </label>
                  </div>
                  <div className="flex-1">
                    <p className="font-medium text-gray-800 truncate">{cat.category.name}</p>
                    <p className="text-xs text-gray-500">ID: {cat.id}</p>
                  </div>
                  <button
                    onClick={() => handleDeleteDisplayCat(cat.category.id)}
                    className="text-red-500 hover:text-red-700 p-2 rounded-full hover:bg-red-50"
                  >
                    <FiTrash2 size={18} />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}