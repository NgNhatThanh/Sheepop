import './App.css';
import axios from 'axios';
import { useState } from 'react';


function App() {

  axios.get('/test')
  .then(r => console.log(r.data))

  return (
    
    <div>
      test
    </div>

  );
}

export default App;
