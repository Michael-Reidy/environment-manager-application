import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Environment e2e test', () => {
  const environmentPageUrl = '/environment-my-suffix';
  const environmentPageUrlPattern = new RegExp('/environment-my-suffix(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const environmentSample = {};

  let environment;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/environments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/environments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/environments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (environment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/environments/${environment.id}`,
      }).then(() => {
        environment = undefined;
      });
    }
  });

  it('Environments menu should load Environments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('environment-my-suffix');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Environment').should('exist');
    cy.url().should('match', environmentPageUrlPattern);
  });

  describe('Environment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(environmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Environment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/environment-my-suffix/new$'));
        cy.getEntityCreateUpdateHeading('Environment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/environments',
          body: environmentSample,
        }).then(({ body }) => {
          environment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/environments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/environments?page=0&size=20>; rel="last",<http://localhost/api/environments?page=0&size=20>; rel="first"',
              },
              body: [environment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(environmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Environment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('environment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentPageUrlPattern);
      });

      it('edit button click should load edit Environment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Environment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentPageUrlPattern);
      });

      it('edit button click should load edit Environment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Environment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentPageUrlPattern);
      });

      it('last delete button click should delete instance of Environment', () => {
        cy.intercept('GET', '/api/environments/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('environment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentPageUrlPattern);

        environment = undefined;
      });
    });
  });

  describe('new Environment page', () => {
    beforeEach(() => {
      cy.visit(`${environmentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Environment');
    });

    it('should create an instance of Environment', () => {
      cy.get(`[data-cy="name"]`).type('Peso Salad Cotton').should('have.value', 'Peso Salad Cotton');

      cy.get(`[data-cy="startDate"]`).type('2023-02-22T15:01').blur().should('have.value', '2023-02-22T15:01');

      cy.get(`[data-cy="endDate"]`).type('2023-02-22T12:45').blur().should('have.value', '2023-02-22T12:45');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        environment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', environmentPageUrlPattern);
    });
  });
});
