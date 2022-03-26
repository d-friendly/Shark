import React, {useState, useEffect} from "react";

export default function ItemRow(props) {
    const [formData, setFormData] = useState({ name: '', value: '' });
    
    const handleChange = e => {
        e.preventDefault();
        // console.log(formData);

        //use this in challenge.js probably
        // console.log({ ...formData, [e.target.name]: e.target.value })
        setFormData({[e.target.name]: e.target.value });
        // console.log(formData);
    }
    useEffect(() => {
        // console.log("use effect")
        if (props.onChange) {
          props.onChange(formData)
        }
      }, [formData.name, formData.value])
    return(
        <>
            <td>{props.id}</td>
            <td>{props.name}</td>
            <td>{props.stock}</td>
            <td>{props.capacity}</td>
            <td>
                <form>
                    <label>
                        <input type="text" name={props.id} onChange={handleChange} />
                    </label>
                </form>
            </td>
        </>
    );

}