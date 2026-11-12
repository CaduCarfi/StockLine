import { Alerta, AlertaService } from './alerta.service';

describe('AlertaService', () => {
  let service: AlertaService;
  let recebidos: (Alerta | null)[];

  beforeEach(() => {
    service = new AlertaService();
    recebidos = [];
    service.alerta$.subscribe(a => recebidos.push(a));
  });

  it('emite alerta de sucesso com a mensagem informada', () => {
    service.sucesso('ok');
    expect(recebidos).toEqual([{ tipo: 'sucesso', mensagem: 'ok' }]);
  });

  it('emite alerta de erro com a mensagem informada', () => {
    service.erro('falhou');
    expect(recebidos).toEqual([{ tipo: 'erro', mensagem: 'falhou' }]);
  });

  it('emite null ao limpar', () => {
    service.limpar();
    expect(recebidos).toEqual([null]);
  });

  it('preserva a sequência de emissões', () => {
    service.sucesso('a');
    service.erro('b');
    service.limpar();
    expect(recebidos.map(r => r?.tipo ?? null)).toEqual(['sucesso', 'erro', null]);
  });
});
