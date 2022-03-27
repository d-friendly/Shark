
import React, {useEffect, useState, useCallback, API, forwardRef} from "react";
// import ItemRow from "../components/ItemRow";



 
export default function Challenge() {
  const [show, setShow] = useState(false) //toggles control for showing table rows
  const [filteredJsonState, setFilteredJson] = useState([]) //state containing current 25% or lower stock
  const [displayState, setDisplay] = useState([]) //state that cointains ReactFragment to show based on show
  const [totalCost, setTotalCost ] = useState([]) //total cost state

  const toggle = previous => !previous //toggle control 

  const handleSubmit = (e) => {
    e.preventDefault()
   
    /* Debug statements
    console.log(e)
    console.log(e.target[0].value);
    console.log(e. target[1].name);
    console.log(e.target.length);
    */
    let inputs = []
    for(let i = 0; i < e.target.length-2; i++){
      let id = e.target[i].name
      let value = e.target[i].value
      if(!isNaN(value) && value != ''){
        inputs.push({id,value})
      }
    }
    getReorderCost(inputs)
  }

  /**
   * if filteredJsonState is set displayState to be a list of ItemRows
   * Decided to not make a new component due to issues with accessing input field.
   * Ultimately, fixed this issue by moving the form tag outside the button tags. 
   */
  useEffect(() =>{
    setDisplay(filteredJsonState.map(data => {
      const { Name, Stock, Capacity, ID } = data 
      if(Stock/Capacity < .25) {
        // console.log(data);
        return (
          <React.Fragment key={ID}>
            <tr>
              <td>{ID}</td>
              <td>{Name}</td>
              <td>{Stock}</td>
              <td>{Capacity}</td>
              <td>
                  <div className="input">
                    <label>
                      <input type="text" name={ID} />
                    </label>
                  </div>

              </td>
            </tr>
            </React.Fragment>
        )
      }
    }))
  },[filteredJsonState])
  
 
  /**
   * Fetchs /restoc-cost with a POST request
   * @param {Array of name, value pair to be used as parameters for Post Fetch} inputs 
   */
  async function getReorderCost(inputs) {
    // console.log(inputs)
    let url='http://localhost:4567/restock-cost'
    const options = {
      method: 'POST',
      body: JSON.stringify( inputs )  
    };
    const response = await fetch(url,options);
    if (!response.ok) {
      const message = `An error has occured: ${response.status}`
      throw new Error(message)
    }
    const json = await response.json()
    setTotalCost(json)
    // console.log(json) Test Statement
  }

  /**
   * Gets Low Stock items and sets the respective state.
   */
  async function getLowStockItems() {
    //controls if table is shown / data is fetched.
    toggle ? setShow(true) : setShow(toggle)
  
    let url = 'http://localhost:4567/low-stock';
    const response = await fetch(url);
    if (!response.ok) { 
      const message = `An error has occured: ${response.status}`;
      throw new Error(message);
    }
    const json = await response.json();
    setFilteredJson(json); 
  }

  return (
    <>
      <form onSubmit={e => {handleSubmit(e)}}>
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
            {show? 
              displayState
            :
              <></>
            }
          </tbody>
        </table>
        <div>Total Cost: 
          {totalCost}  
        </div>
        <button type="button" onClick={getLowStockItems}>Get Low-Stock Items</button>
        <button>Determine Re-Order Cost</button>
      </form>
    </>
  );
}

