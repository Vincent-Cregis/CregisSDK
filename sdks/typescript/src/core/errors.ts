export class CregisError extends Error {
  public constructor(message: string, options?: ErrorOptions) {
    super(message, options);
    this.name = new.target.name;
  }
}

export class CregisClientError extends CregisError {}

export class CregisHttpError extends CregisError {
  public readonly status: number;
  public readonly statusText: string;
  public readonly responseBody: string;

  public constructor(status: number, statusText: string, responseBody: string) {
    super(`Cregis HTTP error: [${status}] ${statusText}`);
    this.status = status;
    this.statusText = statusText;
    this.responseBody = responseBody;
  }
}

export class CregisApiError extends CregisError {
  public readonly code: string;
  public readonly apiMessage: string | undefined;

  public constructor(code: string, apiMessage?: string) {
    super(`Cregis API error: [${code}] ${apiMessage ?? ""}`.trimEnd());
    this.code = code;
    this.apiMessage = apiMessage;
  }
}
