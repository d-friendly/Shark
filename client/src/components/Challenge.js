
import React, {useEffect, useState, useCallback, API} from "react";
import ItemRow from "../components/ItemRow";

export default function Challenge() {
  const [checked, setChecked] = useState(false);
  const jsonInitialState ="null";
  const [jsonState, setJson] = useState(jsonInitialState);
  const toggle = previous => !previous;
  
    
  function getList(){
    return jsonState.map((item,index)=>{
      return <ItemRow/>
    })
  }

  async function getLowStockItems(postOrGet) {
    if(toggle){ 
      setChecked(true);
    }else{
      setChecked(toggle);
    }
  
    
    console.log({checked});
    let url="";
    if(postOrGet == "GET"){
      url = 'http://localhost:4567/low-stock';
      const response = await fetch(url);
      if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
      }
      const json = await response.json();
      console.log(json);
      setJson(json);
      console.log("Json State below");
      console.log(jsonState);
      console.log("json above")
    }else{
      url = 'http://localhost:4567/restock-cost';
      const settings = {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
  
      };
      const response = await fetch(url,settings);
      if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
      }
      const json = await response.json();
      
    }
    // { jsonState.map(
    //   (info)=>{
    //     return <ItemRow name={info.name} stock={info.stock} capacity={info.capacity} id={info.id}/> 
    //   })
    // }
  }

  
  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody>
          {getList}
          
          
          
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: </div>
      {/* 
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      <button onClick={()=>getLowStockItems("GET")}>Get Low-Stock Items</button>
      <button onClick={()=>getLowStockItems("POST")}>Determine Re-Order Cost</button>
      
    </>
  );
}
