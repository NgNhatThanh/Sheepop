import { useState, useEffect } from "react";
import { fetchWithAuth } from "../../../util/AuthUtil";
import { BASE_API_URL } from "../../../constants";
import { toast } from "react-toastify";
import Modal from "../../common/Modal";
import AddressInputModal from "./AddressInputModal";

export default function AddressPage(){
    const [addressList, setAddressList] = useState([])
    const [newAddress, setNewAddress] = useState({ 
      receiverName: "", 
      phoneNumber: "", 
      detail: "", 
      ward: "", 
      district: "", 
      province: "", 
      primary: false 
    });
    const [modalInfo, setModalInfo] = useState(null)  
    const [deleteAddress, setDeleteAddress] = useState(null)
  
    const fetchAddressList = async () => {
        fetchWithAuth(`${BASE_API_URL}/v1/user/address/get-list`, window.location, true)
            .then(res => res.json())
            .then(data => {
                console.log(data)
                setAddressList(data)
            })
    }

    const setPrimary = (addressId) => {
      fetchWithAuth(`${BASE_API_URL}/v1/user/address/set-primary?addressId=${addressId}`, window.location, true, {
        method: "POST"
      })
        .then(res => res.json())
        .then(res => {
          if(res.message) toast.error(res.message)
          else{
            fetchAddressList()
          }
        })
    }

    const handleDeleteAddress = (addressId) => {
      fetchWithAuth(`${BASE_API_URL}/v1/user/address/delete?addressId=${addressId}`, window.location, true, {
        method: "POST"
      })
        .then(res => res.json())
        .then(res => {
          if(res.message) toast.error(res.message)
          else{
            setAddressList(prev => prev.filter(addr => addr.id !== addressId))
            toast.success('Xóa địa chỉ thành công')
          }
        })
    }

    useEffect(() => {
        fetchAddressList()
    }, [])

  return (
    <div className="p-4 w-300 mx-auto min-h-screen bg-white shadow-md rounded-md">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Địa chỉ</h2>
        <button 
          className="cursor-pointer bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          onClick={() => setModalInfo(newAddress)}
        >
          + Thêm địa chỉ
        </button>
      </div>

      {addressList.map((addr) => (
        <div key={addr.id} className="mb-4 p-4 border rounded-lg shadow">
          <div className="flex justify-between">
            <div>
              <p className="font-semibold">{addr.receiverName} <span className="text-gray-500">({addr.phoneNumber})</span></p>
              <p>{addr.detail}</p>
              <p> {addr.ward.name}, {addr.district.name}, {addr.province.name} </p>
              {addr.primary && <span className="text-md text-red-500 text-sm font-semibold border-2 border-solid border-red-100">Mặc định</span>}
            </div>
            <div className="flex flex-col items-end gap-2">
              <div className="text-blue-500 ">
                <button 
                  className="cursor-pointer mr-4"
                  onClick={() => {
                    setModalInfo({
                      addressId: addr.id,
                      receiverName: addr.receiverName, 
                      phoneNumber: addr.phoneNumber, 
                      detail: addr.detail, 
                      ward: addr.ward.name, 
                      district: addr.district.name, 
                      province: addr.province.name, 
                      primary: addr.primary 
                    })
                  }}
                >
                  Cập nhật
                </button>
                {!addr.primary && <button className="cursor-pointer" onClick={() => setDeleteAddress(addr.id)}>Xóa</button>}
              </div>
              {!addr.primary && (<button 
                  className="cursor-pointer border-1 border-solid p-1 hover:bg-gray-100 rounded-sm"
                  onClick={() => setPrimary(addr.id)}
                >
                  Đặt làm mặc định
                </button>)}
            </div>
          </div>
        </div>
      ))}

      <Modal
        open={deleteAddress !== null}
        title='Bạn muốn xóa địa chỉ này'
        content='Hành động này không thể hoàn tác'
        onClose={() => setDeleteAddress(null)}
        onSucess={() => {
          handleDeleteAddress(deleteAddress)
          setDeleteAddress(null)
        }}
      />

      {modalInfo && <AddressInputModal 
        info={modalInfo} 
        setInfo={setModalInfo} 
        setClose={() => setModalInfo(null)}
        onSuccess={() => {
          setModalInfo(null)
          fetchAddressList()
        }}
      />}
    </div>
  );
};
