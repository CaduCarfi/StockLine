import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService } from './api.service';

describe('ApiService', () => {
  const BASE = 'http://localhost:8080';
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('monta URL com baseUrl no GET e devolve corpo tipado', () => {
    const payload = [{ id: 'a', nome: 'X' }];
    let resposta: unknown;
    service.get<typeof payload>('/medicamentos').subscribe(r => (resposta = r));

    const req = http.expectOne(`${BASE}/medicamentos`);
    expect(req.request.method).toBe('GET');
    req.flush(payload);

    expect(resposta).toEqual(payload);
  });

  it('envia body no POST/PUT/PATCH e repassa resposta', () => {
    const bodyPost = { nome: 'A' };
    let postResp: unknown;
    service.post<{ id: string }>('/categorias', bodyPost).subscribe(r => (postResp = r));
    const reqPost = http.expectOne(`${BASE}/categorias`);
    expect(reqPost.request.method).toBe('POST');
    expect(reqPost.request.body).toEqual(bodyPost);
    reqPost.flush({ id: '1' });
    expect(postResp).toEqual({ id: '1' });

    const bodyPut = { nome: 'B' };
    service.put('/categorias/1', bodyPut).subscribe();
    const reqPut = http.expectOne(`${BASE}/categorias/1`);
    expect(reqPut.request.method).toBe('PUT');
    expect(reqPut.request.body).toEqual(bodyPut);
    reqPut.flush({});

    const bodyPatch = { delta: 5 };
    service.patch('/medicamentos/1/quantidade', bodyPatch).subscribe();
    const reqPatch = http.expectOne(`${BASE}/medicamentos/1/quantidade`);
    expect(reqPatch.request.method).toBe('PATCH');
    expect(reqPatch.request.body).toEqual(bodyPatch);
    reqPatch.flush({});
  });

  it('DELETE envia método correto na URL informada', () => {
    service.delete('/medicamentos/42').subscribe();
    const req = http.expectOne(`${BASE}/medicamentos/42`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('converte erro HTTP em Error com a mensagem do backend', () => {
    let erro: Error | null = null;
    service.get('/falha').subscribe({ error: (e: Error) => (erro = e) });

    const req = http.expectOne(`${BASE}/falha`);
    req.flush({ status: 404, mensagem: 'Recurso não encontrado' },
      { status: 404, statusText: 'Not Found' });

    expect(erro).toBeInstanceOf(Error);
    expect(erro!.message).toBe('Recurso não encontrado');
  });

  it('usa mensagem padrão quando corpo de erro não tem campo "mensagem"', () => {
    let erro: Error | null = null;
    service.get('/falha').subscribe({ error: (e: Error) => (erro = e) });

    const req = http.expectOne(`${BASE}/falha`);
    req.flush(null, { status: 500, statusText: 'Server Error' });

    expect(erro!.message).toBe('Erro inesperado. Tente novamente.');
  });

  it('também usa mensagem padrão quando corpo é objeto sem a chave esperada', () => {
    let erro: Error | null = null;
    service.post('/x', {}).subscribe({ error: (e: Error) => (erro = e) });

    const req = http.expectOne(`${BASE}/x`);
    req.flush({ outraChave: 'ignorada' }, { status: 400, statusText: 'Bad Request' });

    expect(erro!.message).toBe('Erro inesperado. Tente novamente.');
  });
});
