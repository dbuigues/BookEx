const modal = document.getElementById('modal');
const agregarListaBtn = document.getElementById('agregar-lista');
const cerrar = document.getElementById('cerrar');

agregarListaBtn.addEventListener("click", () => {
    modal.style.display = "flex";
});

cerrar.addEventListener("click", () => {
    modal.style.display = "none";
});

window.addEventListener("click" ,(e) => {
    if(e.target==modal){
        modal.style.display = "none";
    }
});

const crearListaBtn = document.getElementById('crear-lista');

crearListaBtn.addEventListener("click", () => {
    const nombreListaInput = document.getElementById('nombre-lista').value;
    const descripcionListaInput = document.getElementById('descripcion-lista').value;

    if (!nombreListaInput || !descripcionListaInput){
        alert("Por favor, rellena todos los campos.");
        return;
    }

    const contenedorListas = document.getElementById('listas-container');

    const listaDiv = document.createElement("div");

    listaDiv.textContent = `Nombre: ${nombreListaInput} - Descripción: ${descripcionListaInput}`;
    
    contenedorListas.appendChild(listaDiv);
})