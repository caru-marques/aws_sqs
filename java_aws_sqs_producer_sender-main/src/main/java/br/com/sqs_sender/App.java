package br.com.sqs_sender;

import java.time.LocalDate;

import br.com.sqs_sender.services.SQSService;

public class App 
{
  public static void main( String[] args )
  {
    SQSService.sendMessage("Mensagem personalizada da Caru enviada em: " + LocalDate.now());
  }
}
