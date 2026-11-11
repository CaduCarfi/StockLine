import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Alerta {
  tipo: 'sucesso' | 'erro';
  mensagem: string;
}

@Injectable({ providedIn: 'root' })
export class AlertaService {
  private subject = new Subject<Alerta | null>();
  alerta$ = this.subject.asObservable();

  sucesso(mensagem: string): void {
    this.subject.next({ tipo: 'sucesso', mensagem });
  }

  erro(mensagem: string): void {
    this.subject.next({ tipo: 'erro', mensagem });
  }

  limpar(): void {
    this.subject.next(null);
  }
}
