const BotonCrearLista = document.getElementById('agregar-lista');
const BotoncerrarModal = document.getElementById('cerrar');

if (sessionStorage.getItem('sesionActiva')!=null) {
  sesion = sessionStorage.getItem('sesionActiva');
  BotonCrearLista.style.display = 'block';
}else{
  BotonCrearLista.style.display = 'none';
}

BotonCrearLista.addEventListener('click', () => { modal.style.display = 'block'; });
BotoncerrarModal.addEventListener('click', () => { modal.style.display = 'none'; });