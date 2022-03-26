
import React, {useEffect, useState, useCallback, API} from "react";
import ItemRow from "../components/ItemRow";


 
export default function Challenge() {
  const [checked, setChecked] = useState(false);
  const jsonInitialState ="null";
  const [jsonState, setJson] = useState([]);
  const [filteredJsonState, setFilteredJson] = useState([]);
  const toggle = previous => !previous;
  const [tableRow, setTableRow] = useState(["Name",0,0,0,0])
  // const [orderAmt, setOrderAmt] = userState
  
  const eventhandler = data => {

    console.log(data)  
  }
  useEffect(() =>{
    let filteredList = jsonState;
    
    // filteredList=filteredList.filter(jsonItem => console.log(jsonItem));
  },[jsonState,filteredJsonState])


  async function getLowStockItems(postOrGet) {
    if(toggle){ 
      setChecked(true);
    }else{
      setChecked(toggle);
    }
    let url="";
    
    if(postOrGet == "GET"){
      url = 'http://localhost:4567/low-stock';
      const response = await fetch(url);
      if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
      }
      const json = await response.json();
      // console.log(json);
      let list = []
      for(var i=0; i < json.length;i++){
        // console.log(json[i].Stock/json[i].Capacity) ;
        if(json[i].Stock/json[i].Capacity < .25) {
          // console.log(json[i]);
          // console.log(displayList());
          
          
          list.push(
            <tr><ItemRow name={json[i].Name} stock={json[i].Stock} capacity={json[i].Capacity} id={json[i].ID} onChange={eventhandler}/></tr>
          )
          
          // console.log(list);
          
          //update state here 
        }
      }
      console.log(list);
      setFilteredJson(list);
      // console.log(filteredJsonState)
      setJson(json);
      // console.log("Json State below");
      // console.log(jsonState);
      // console.log("json above")
      
      // const newJson = {jsonState}.filter((jsonItem) => jsonItem.stock / jsonItem.capacity)
    
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
          {checked? 
            filteredJsonState.map(data =>{
              return data;
            })
          :
            <></>
          }
          {/* <ItemRow/> */}
          
          
          
          
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
