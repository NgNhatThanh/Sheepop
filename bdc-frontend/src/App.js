import React from 'react';
import {BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './tmp/Home'
import HandleRedirect from './tmp/HandleRedirect'

function App() {
  return (
    <div>
      <h1>My React App</h1>
      <Routes>
        <Route path='/home' element={<Home/>} />
        <Route path='/redirect/auth' element={<HandleRedirect/>} />
      </Routes>
    </div>
  );
}

export default App;