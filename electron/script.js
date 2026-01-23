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
    const nombreListaInput = document.getElementById('nombre-lista');
    const descripcionListaInput = document.getElementById('descripcion-lista');

    if (!nombreListaInput.value.trim() || !descripcionListaInput.value.trim()){
        alert("Por favor, rellena todos los campos.");
        return;
    }

    const contenedorListas = document.getElementById('listas-container');
    
    if (!contenedorListas) {
        console.error("No se encuentra el elemento 'listas-container'");
        return;
    }

    const listaDiv = document.createElement("div");
    listaDiv.className = "lista-item"; 
    
    listaDiv.innerHTML = `
        <h3>${nombreListaInput.value}</h3>
        <p>${descripcionListaInput.value}</p>
        <button class="eliminar-lista">Eliminar Lista</button>
    `;
    
    contenedorListas.appendChild(listaDiv);
    
    // Limpiar los inputs
    nombreListaInput.value = '';
    descripcionListaInput.value = '';
    
    // Cerrar la pestaña de introducir información
    const formularioContainer = document.getElementById('formulario-container'); // o el ID que tengas
    if (formularioContainer) {
        formularioContainer.style.display = 'none';
    }
});

// Delegación de eventos para eliminar listas
const contenedorListas = document.getElementById('listas-container');

if (contenedorListas) {
    contenedorListas.addEventListener('click', (event) => {
        if (event.target.classList.contains('eliminar-lista')) {
            const listaItem = event.target.closest('.lista-item');
            if (listaItem) {
                listaItem.remove();
            }
        }
    });
}