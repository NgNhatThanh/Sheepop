import React, { useEffect, useState, lazy } from 'react';
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import { checkAuthenticated } from './common/AuthUtil';

const HomePage = lazy(() => import("./pages/home/index.tsx"))
const LoginPage = lazy(() => import("./pages/login/index.tsx"))
const RegisterPage = lazy(() => import("./pages/register/index.tsx"))
const HandleRedirect = lazy(() => import("./pages/login/HandleOauthRedirect.js"))
const MyShopLayout = lazy(() => import ('./pages/myshop/MyShopLayout.jsx'))
const ShopProducts = lazy(() => import('./pages/myshop/product/ShopProducts.jsx'));
const ShopDashboard = lazy(() => import('./pages/myshop/ShopDashboard.jsx'));


function App() {

  const [isAuthenticated, setIsAuthenticated] = useState(null)

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await checkAuthenticated()
      setIsAuthenticated(authenticated)
    }
    checkAuth()
  }, [])

  if(isAuthenticated === null){
    return(
      <div>
        Loading...
      </div>
    )
  }

  return (
      <Router>
        <Routes>
          <Route exact path='/' element={<HomePage isAuthenticated={isAuthenticated}/>}/>
          <Route exact path='/login' element={isAuthenticated ? <HomePage isAuthenticated={true}/> : <LoginPage/>}/>
          <Route exact path='/register' element={isAuthenticated ? <HomePage isAuthenticated={true}/> : <RegisterPage/>}/>
          <Route path='/redirect/:target' element={<HandleRedirect/>}/>
          {/* <Route path='/myshop' element={<Navigate to='/myshop/dashboard'/>}/> */}
          <Route path='/myshop' element={<MyShopLayout isAuthenticated={isAuthenticated}/>}>
            <Route path='dashboard' element={<ShopDashboard/>}/>
            <Route path='products' element={<ShopProducts/>}/>
          </Route>
        </Routes>
      </Router>
  );
}

export default App;