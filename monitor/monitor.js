// Pantalla externa: pide un token y consulta /api/v1/pendientes cada 15 segundos
'use strict';

const API = 'http://localhost:8080/api/v1';
let token = null;   // en memoria: si se recarga la página, hay que volver a conectarse

const $ = id => document.getElementById(id);

$('ingreso').addEventListener('submit', async evento => {
  evento.preventDefault();
  $('mensaje').textContent = 'Conectando…';
  try {
    const respuesta = await fetch(`${API}/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: $('email').value, password: $('password').value }),
    });
    const datos = await respuesta.json();
    if (!respuesta.ok) throw new Error(datos.detail || 'No se pudo conectar');
    token = datos.token;
    $('ingreso').hidden = true;
    $('panel').hidden = false;
    $('mensaje').textContent = '';
    actualizar();
    setInterval(actualizar, 15000);
  } catch (e) {
    // Si sale «Failed to fetch», revise CORS: el origen de esta página debe estar en soporteya.cors.origenes
    $('mensaje').textContent = e.message;
  }
});

async function actualizar() {
  const respuesta = await fetch(`${API}/pendientes`, { headers: { Authorization: `Bearer ${token}` } });
  if (respuesta.status === 401) {
    $('mensaje').textContent = 'El token expiró. Recargue la página y conéctese de nuevo.';
    return;
  }
  const pendientes = await respuesta.json();
  $('abiertos').textContent = pendientes.filter(t => t.estado === 'ABIERTO').length;
  $('enCurso').textContent = pendientes.filter(t => t.estado === 'EN_CURSO').length;
  $('vencidos').textContent = pendientes.filter(t => t.vencido).length;
  $('lista').replaceChildren(...pendientes.slice(0, 8).map(t => {
    const li = document.createElement('li');
    li.className = 'list-group-item d-flex justify-content-between' + (t.vencido ? ' list-group-item-danger' : '');
    li.textContent = `${t.codigo} · ${t.asunto}`;
    const quien = document.createElement('span');
    quien.className = 'small';
    quien.textContent = t.tecnico ?? 'Sin técnico';
    li.append(quien);
    return li;
  }));
}