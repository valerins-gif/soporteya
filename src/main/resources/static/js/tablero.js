// SoporteYa · tablero que se actualiza solo y toma tickets sin recargar (Unidad 4)
'use strict';

const contenedor = document.getElementById('tablero');
const estado = document.getElementById('estadoTablero');
const esTecnico = contenedor.dataset.tecnico === 'true';
const CADA_MS = 20000;

const formatoFecha = new Intl.DateTimeFormat('es-CO', { day: 'numeric', month: 'short', hour: 'numeric', minute: '2-digit' });
const ETIQUETA_PRIORIDAD = { BAJA: 'Baja', MEDIA: 'Media', ALTA: 'Alta', CRITICA: 'Crítica' };

// Crea un elemento con clase y texto. Usar textContent evita que un asunto con <script> se ejecute (XSS).
function el(etiqueta, clase, texto) {
  const nodo = document.createElement(etiqueta);
  if (clase) nodo.className = clase;
  if (texto !== undefined && texto !== null) nodo.textContent = texto;
  return nodo;
}

function tarjeta(t, sePuedeTomar) {
  const card = el('div', 'card card-body tarjeta-ticket mb-2 py-2' + (t.vencido ? ' vencido' : ''));
  const fila = el('div', 'd-flex justify-content-between small');
  const enlace = el('a', null, t.codigo);
  enlace.href = `/tickets/${t.id}`;
  fila.append(enlace, el('span', `badge prioridad-${t.prioridad}`, ETIQUETA_PRIORIDAD[t.prioridad]));
  card.append(fila, el('div', 'fw-semibold', t.asunto), el('div', 'small text-muted', `${t.categoria} · ${t.solicitante}`));
  card.append(el('div', 'small ' + (t.vencido ? 'text-danger fw-semibold' : 'text-muted'),
      'Límite: ' + formatoFecha.format(new Date(t.fechaLimite))));
  if (t.tecnico) card.append(el('div', 'small', 'Técnico: ' + t.tecnico));
  if (sePuedeTomar && esTecnico) {
    const boton = el('button', 'btn btn-sm btn-primary mt-2 align-self-start', 'Tomar');
    boton.type = 'button';
    boton.addEventListener('click', () => tomar(t.id, boton));
    card.append(boton);
  }
  return card;
}

function columna(titulo, lista, sePuedeTomar) {
  const col = el('div', 'col-lg-4');
  const caja = el('div', 'columna');
  const h2 = el('h2', 'h6 d-flex justify-content-between');
  h2.append(el('span', null, titulo), el('span', 'badge text-bg-secondary', lista.length));
  caja.append(h2);
  if (lista.length === 0) caja.append(el('p', 'small text-muted', 'Nada por aquí.'));
  lista.forEach(t => caja.append(tarjeta(t, sePuedeTomar)));
  col.append(caja);
  return col;
}

async function actualizar() {
  try {
    const datos = await SY.get('/api/web/tablero');
    contenedor.replaceChildren(
      columna('Abiertos', datos.abiertos, true),
      columna('En curso', datos.enCurso, false),
      columna('Resueltos sin confirmar', datos.resueltos, false));
    estado.textContent = 'Actualizado a las ' + new Date().toLocaleTimeString('es-CO');
  } catch (e) {
    estado.textContent = e.message;
  }
}

async function tomar(id, boton) {
  boton.disabled = true;
  boton.textContent = 'Tomando…';
  try {
    const ticket = await SY.post(`/api/web/tablero/${id}/tomar`);
    SY.aviso(`Usted tomó el ticket ${ticket.codigo}`);
  } catch (e) {
    SY.aviso(e.message, 'danger');      // 409: ya lo tomó otro, no es de su especialidad…
  }
  await actualizar();
}

actualizar();
setInterval(actualizar, CADA_MS);