import { CommonModule } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { Alerta, AlertaService } from '../../services/alerta.service';
import { AlertComponent } from './alert.component';

describe('AlertComponent', () => {
  let alertas$: Subject<Alerta | null>;
  let alertaService: { alerta$: Subject<Alerta | null>; limpar: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    alertas$ = new Subject<Alerta | null>();
    alertaService = { alerta$: alertas$, limpar: vi.fn() };
    TestBed.configureTestingModule({
      imports: [CommonModule],
      declarations: [AlertComponent],
      providers: [{ provide: AlertaService, useValue: alertaService }],
    });
  });

  afterEach(() => vi.useRealTimers());

  it('não renderiza nada sem alerta ativo', () => {
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('.alert')).toBeNull();
  });

  it('propaga alerta emitido pelo service para o componente', () => {
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'salvo' });
    expect(fixture.componentInstance.alerta).toEqual({ tipo: 'sucesso', mensagem: 'salvo' });

    alertas$.next({ tipo: 'erro', mensagem: 'falha' });
    expect(fixture.componentInstance.alerta).toEqual({ tipo: 'erro', mensagem: 'falha' });
  });

  it('renderiza mensagem e classe quando o alerta é visível', () => {
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'salvo' });
    fixture.componentRef.changeDetectorRef.markForCheck();
    fixture.detectChanges();

    const el = (fixture.nativeElement as HTMLElement).querySelector('.alert') as HTMLElement;
    expect(el).not.toBeNull();
    expect(el.classList.contains('alert--sucesso')).toBe(true);
    expect(el.textContent).toContain('salvo');
  });

  it('aplica classe de erro para alertas do tipo "erro"', () => {
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'erro', mensagem: 'falha' });
    fixture.componentRef.changeDetectorRef.markForCheck();
    fixture.detectChanges();

    const el = (fixture.nativeElement as HTMLElement).querySelector('.alert') as HTMLElement;
    expect(el).not.toBeNull();
    expect(el.classList.contains('alert--erro')).toBe(true);
  });

  it('fecha o alerta automaticamente após 4s', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'x' });
    expect(fixture.componentInstance.alerta).not.toBeNull();

    vi.advanceTimersByTime(4000);
    expect(fixture.componentInstance.alerta).toBeNull();
    expect(alertaService.limpar).toHaveBeenCalled();
  });

  it('fechar() limpa o alerta e aciona service.limpar', () => {
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'x' });
    expect(fixture.componentInstance.alerta).not.toBeNull();

    fixture.componentInstance.fechar();
    expect(fixture.componentInstance.alerta).toBeNull();
    expect(alertaService.limpar).toHaveBeenCalled();
  });

  it('reinicia o timer quando um novo alerta chega antes do timeout', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'a' });
    vi.advanceTimersByTime(2000);
    alertas$.next({ tipo: 'erro', mensagem: 'b' });
    vi.advanceTimersByTime(3000); // 5s desde o primeiro, 3s desde o segundo
    expect(fixture.componentInstance.alerta?.mensagem).toBe('b');

    vi.advanceTimersByTime(1000); // 4s desde o segundo
    expect(fixture.componentInstance.alerta).toBeNull();
  });

  it('não aciona timer quando a emissão é null (limpeza)', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next(null);
    vi.advanceTimersByTime(10000);
    expect(fixture.componentInstance.alerta).toBeNull();
    expect(alertaService.limpar).not.toHaveBeenCalled();
  });

  it('cancela timer ao destruir o componente', () => {
    vi.useFakeTimers();
    const fixture = TestBed.createComponent(AlertComponent);
    fixture.detectChanges();
    alertas$.next({ tipo: 'sucesso', mensagem: 'x' });
    fixture.destroy();
    expect(() => vi.advanceTimersByTime(5000)).not.toThrow();
    expect(alertaService.limpar).not.toHaveBeenCalled();
  });
});
